package com.iftm.client.service;

import com.iftm.client.dto.ClientDTO;
import com.iftm.client.entities.Client;
import com.iftm.client.repositories.ClientRepository;
import com.iftm.client.services.ClientService;
import com.iftm.client.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
public class ClientServiceTest {


    @InjectMocks
    private ClientService servico;

    @Mock
    private ClientRepository repositorio;

//    delete deveria
//    ◦ retornar vazio quando o id existir
    @DisplayName("Testar se o método deleteById apaga um registro e não retorna outras informações")
    @Test
    public void testarApagarPorIdTemSucessoComIdExistente() {
        //cenário
        long idExistente = 1;
        //configurando mock : definindo que o método deleteById não retorna nada para esse id.
        Mockito.doNothing().when(repositorio).deleteById(idExistente);

        Assertions.assertDoesNotThrow(() -> {
            servico.delete(idExistente);
        });

        Mockito.verify(repositorio, times(1)).deleteById(idExistente);
    }

    //    delete deveria
    // lançar uma EmptyResultDataAccessException quando o id não existir

    @DisplayName("Testa se o método update retorna um ResourceNotFound")
    @Test
    public void testarSeOMetodoDeleteRetornaErro() {
        Long id = 1982L;

        Mockito.doThrow(ResourceNotFoundException.class).when(repositorio).deleteById(id);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> servico.delete(id));
        Mockito.verify(repositorio , times(1)).deleteById(id);
    }

//    findAllPaged deveria retornar uma página com todos os clientes (e chamar o método
//    findAll do repository)

    @DisplayName("Testa se o método findAllPagedRetorna corretamente")
    @Test
    public void testaSeOMetodoFindAllPagedRetornaCorretamente() {
        List<Client> clientes = new ArrayList<>(Arrays.asList(
                new Client(1L,"Felipe Guimarães", "123123123123", 242.0, Instant.now(), 1),
                new Client(2L,"Ana Paula", "41412414142124", 1354.0, Instant.now(), 4),
                new Client(3L,"Maria", "923823812738", 5030.0, Instant.now(), 5)
        ));

        PageRequest pageRequest = PageRequest.of(0, clientes.size());
        Page<Client> page = new PageImpl<>(clientes);

        Mockito.when(repositorio.findAll(pageRequest)).thenReturn(page);
        Page<ClientDTO> result = servico.findAllPaged(pageRequest);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(clientes.size(), result.getSize());
        Assertions.assertEquals(clientes.get(0).getId(), result.getContent().get(0).getId());
        Assertions.assertEquals(clientes.get(1).getId(), result.getContent().get(1).getId());
        Assertions.assertEquals(clientes.get(2).getId(), result.getContent().get(2).getId());
        Mockito.verify(repositorio , times(1)).findAll(pageRequest);
    }


//    findAllPaged deveria retornar uma página com todos os clientes (e chamar o método
//    findAll do repository)

    @DisplayName("Testa se o método retorna página com todos os clientes")
    @Test
    public void testaRetornoTodosOsClientes() {
        List<Client> clientes = new ArrayList<>(Arrays.asList(
                new Client(2L,"Maria", "394384833", 5235.0, Instant.now(), 5),
                new Client(3L,"Chiquinha", "192298324", 9723.0, Instant.now(), 2)
        ));
        PageRequest pageRequest = PageRequest.of(0, clientes.size());
        Page<Client> page = new PageImpl<>(clientes);
        int tamanho = 2;

        Mockito.when(repositorio.findByIncomeGreaterThan(1500D, pageRequest)).thenReturn(page);
        Page<ClientDTO> resultado = servico.findByIncomeGreaterThan(pageRequest,1500D);

        Assertions.assertEquals(tamanho, resultado.getContent().size());
        Assertions.assertTrue(resultado.getContent().get(0).getIncome()>1500);
        Assertions.assertTrue(resultado.getContent().get(1).getIncome()>1500);
        Mockito.verify(repositorio , times(1))
                .findByIncomeGreaterThan(1500D, pageRequest);
    }


    //findById deveria
    //◦ retornar um ClientDTO quando o id existir

    @DisplayName("Testa se o método findById retorna um Client")
    @Test
    public void testarSeOMetodoFindByIdRetornaUmCliente() {
        long idExistente = 1L;

        Client client = new Client(idExistente,
                "Felipe Guimarães",
                "123123123",
                500.0,
                Instant.parse("1940-02-23T07:00:00Z"),
                1);

        Mockito.when(repositorio.findById(idExistente)).thenReturn(Optional.of(client));

        servico.findById(idExistente);

        Mockito.verify(repositorio, times(1)).findById(idExistente);
    }

    //    findById deveria
    //    lançar ResourceNotFoundException quando o id não existir
    @DisplayName("Testa se o método findById retorna um ResourceNotFound")
    @Test
    public void testarSeOMetodoFindByIdRetornaErro() {
        long idNaoExistente = 67890L;

        Mockito.doThrow(ResourceNotFoundException.class).when(repositorio).findById(idNaoExistente);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> servico.findById(idNaoExistente));

        Mockito.verify(repositorio, times(1)).findById(idNaoExistente);
    }


    //    update deveria
    //    ◦ retornar um ClientDTO quando o id existir
    @DisplayName("Testa se o update retorna um ClientDTO")
    @Test
    public void testarSeOMetodoUpdateRetornaUmClientDTO() {
        long idExistente = 1L;

        Client client = new Client(idExistente,
                "Felipe Guimarães",
                "123123123",
                500.0,
                Instant.parse("1940-02-23T07:00:00Z"),
                1);

        Mockito.when(repositorio.getOne(idExistente)).thenReturn(client);
        Mockito.when(repositorio.findById(idExistente)).thenReturn(Optional.of(client));
        Mockito.when(repositorio.save(client)).thenReturn(client);

        ClientDTO clientDTO = new ClientDTO(idExistente,
                "Felipe Guimarães",
                "123123123",
                500.0,
                Instant.parse("1940-02-23T07:00:00Z"),
                1);

        ClientDTO clientUpdated = servico.update(idExistente, clientDTO);

        assertThat(clientUpdated.getName()).isEqualTo("Felipe Guimarães");
        assertThat(clientUpdated.getCpf()).isEqualTo("123123123");
        assertThat(clientUpdated.getIncome()).isEqualTo(500.0);
        assertThat(clientUpdated.getChildren()).isEqualTo(1);

        Mockito.verify(repositorio, times(1)).save(client);
    }

    //    update deveria
    //    lançar uma ResourceNotFoundException quando o id não existir

    @DisplayName("Testa se o método update retorna um ResourceNotFound")
    @Test
    public void testarSeOMetodoUpdateRetornaErro() {
        Long id = 1244L;

        Mockito.doThrow(ResourceNotFoundException.class).when(repositorio).getOne(id);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> servico.update(id, new ClientDTO()));
        Mockito.verify(repositorio , times(1)).getOne(id);
    }


}
