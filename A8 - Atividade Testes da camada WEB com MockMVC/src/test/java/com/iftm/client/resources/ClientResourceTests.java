package com.iftm.client.resources;

import com.iftm.client.dto.ClientDTO;
import com.iftm.client.entities.Client;
import com.iftm.client.services.ClientService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class ClientResourceTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService service;

    @Test
    @DisplayName("Verificar se o endpoint get/clients/ retorna todos os clientes existentes")
    public void testarEndPointRetornaTodosOsRegistros() throws Exception {

        List<ClientDTO> listaClientes = new ArrayList<ClientDTO>();

        listaClientes.add(new ClientDTO(new Client(7L, "Jose Saramago", "10239254871", 5000.0, Instant.parse("1996-12-23T07:00:00Z"), 0)));
        listaClientes.add(new ClientDTO(new Client(4L, "Carolina Maria de Jesus", "10419244771", 7500.0, Instant.parse("1996-12-23T07:00:00Z"), 0)));
        listaClientes.add(new ClientDTO(new Client(8L, "Toni Morrison", "10219344681", 10000.0,Instant.parse("1940-02-23T07:00:00Z"), 0)));

        Page<ClientDTO> page = new PageImpl<>(listaClientes);

        Mockito.when(service.findAllPaged(Mockito.any())).thenReturn(page);

        int qtdClient = 3;

        ResultActions resultado = mockMvc.perform(get("/clients/").accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfElements").value((qtdClient)));

        resultado.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[?(@.id == '%s')]", 7L).exists())
                .andExpect(jsonPath("$.content[?(@.id == '%s')]", 4L).exists())
                .andExpect(jsonPath("$.content[?(@.id == '%s')]", 8L).exists())
                .andExpect(jsonPath("$.numberOfElements").exists())
                .andExpect(jsonPath("$.numberOfElements").value(qtdClient))
                .andExpect(jsonPath("$.first").exists())
                .andExpect(jsonPath("$.first").value(true));
    }

    @Test
    @DisplayName("Verifica se o endpoint clients/id retorna o cliente correto quando o id existe")
    public void verificaSeOEndpointRetornaOClienteCorretoQuandoOIDExiste() throws Exception {
        long idExistente = 4L;

        ClientDTO cliente = new ClientDTO(idExistente,
                "Carolina Maria de Jesus",
                "10419244771",
                7500.0,
                Instant.parse("1996-12-23T07:00:00Z"),
                0);

        Mockito.when(service.findById(idExistente)).thenReturn(cliente);

        ResultActions resultado = mockMvc.perform(get("/clients/{id}", idExistente)
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idExistente))
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.name").value("Carolina Maria de Jesus"))
                .andExpect(jsonPath("$.cpf").exists())
                .andExpect(jsonPath("$.cpf").value("10419244771"))
                .andExpect(jsonPath("$.income").exists())
                .andExpect(jsonPath("$.income").value(7500.0))
                .andExpect(jsonPath("$.birthDate").exists())
                .andExpect(jsonPath("$.birthDate").value("1996-12-23T07:00:00Z"))
                .andExpect(jsonPath("$.children").exists())
                .andExpect(jsonPath("$.children").value(0));
    }
}
