package br.com.codenation.logstackapi.controller;

import br.com.codenation.logstackapi.builders.UserResquestBuilder;
import br.com.codenation.logstackapi.dto.request.UserRequestDTO;
import br.com.codenation.logstackapi.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static br.com.codenation.logstackapi.util.TestUtil.convertObjectToJsonBytes;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserControllerTest {

    private static String URI = "/api/v1/users";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserServiceImpl userService;

    @Value("${security.oauth2.client.client-id}")
    private String client;

    @Value("${security.oauth2.client.client-secret}")
    private String secret;

    private ObjectMapper objectMapper = new ObjectMapper();
    private JacksonJsonParser parser = new JacksonJsonParser();
    private String token = "";

    @Before
    public void beforeTests() throws Exception {
        token = generateToken();
    }

    @Test
    @Transactional
    public void dadoNovoUsuario_quandoRegistrar_entaoDeveRetornarSucesso() throws Exception {

        UserRequestDTO user = UserResquestBuilder.usuarioComum().build();

        ResultActions perform = mvc.perform(post(URI)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(convertObjectToJsonBytes(user)))
                .andExpect(status().isCreated());

        perform.andExpect(jsonPath("$.email", is(user.getEmail())));
        perform.andExpect(jsonPath("$.fullName", is(user.getFullName())));

    }

    @Test
    @Transactional
    public void dadoDoisUsuariosExistentes_quandoBuscarTodos_entaoDeveRetornarDoisUsuarios() throws Exception {

        this.userService.save(UserResquestBuilder.usuarioComum().build());

        ResultActions perform = mvc.perform(get(URI)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        perform.andExpect(jsonPath("$[0].email", is("admin@admin.com")));
        perform.andExpect(jsonPath("$[0].fullName", is("Administrador")));

        perform.andExpect(jsonPath("$[1].email", is("comum@example.com")));
        perform.andExpect(jsonPath("$[1].fullName", is("Usuário Comum")));

    }

    @Test
    @Transactional
    public void dadoUsuarioComEmailExistente_quandoRegistrar_entaoDeveRetornarErro() throws Exception {

        UserRequestDTO user = UserResquestBuilder.usuarioAdmin().build();

        ResultActions perform = mvc.perform(post(URI)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(convertObjectToJsonBytes(user)))
                .andExpect(status().isBadRequest());

    }

    private String generateToken() throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("username", "admin@admin.com");
        params.add("password", "admin");

        ResultActions login = mvc.perform(
                post("/oauth/token")
                        .params(params)
                        .accept("application/json;charset=UTF-8")
                        .with(httpBasic(client, secret)))
                .andExpect(status().isOk());

        String token = parser.parseMap(login
                .andReturn()
                .getResponse()
                .getContentAsString()).get("access_token").toString();

        return String.format("Bearer %s", token);

    }
}
