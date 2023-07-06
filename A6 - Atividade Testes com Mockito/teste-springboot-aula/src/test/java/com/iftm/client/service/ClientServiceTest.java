package com.iftm.client.services;

import com.iftm.client.dto.ClientDTO;
import com.iftm.client.entities.Client;
import com.iftm.client.repositories.ClientRepository;
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

import javax.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
public class ClientServiceTest {
    @InjectMocks
    ClientService service;

    @Mock
    ClientRepository repository;

    @Test
    @DisplayName("Testa metodo delete quando o id existir")
    public void testeDeleteIdExistente(){
        Long id = 1L;

        Mockito.doNothing().when(repository).deleteById(id);

        Assertions.assertDoesNotThrow(() -> service.delete(id));
        Mockito.verify(repository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Testa metodo delete quando o id não existir")
    public void testeDeleteIdInexistente(){
        Long id = 3432L;
        Mockito.doThrow(ResourceNotFoundException.class).when(repository).deleteById(id);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(id));
        Mockito.verify(repository , times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Testa Metodo findAllPaged que deve retornar uma página com todos os clientes")
    public void testeFindAllPaged(){
        List<Client> clientes = new ArrayList<>(Arrays.asList(
                new Client(1L,"Cayke", "4354353565", 20303D, Instant.now(), 3),
                new Client(2L,"Ryann", "23454365465", 10144D, Instant.now(), 1),
                new Client(3L,"Gerson", "5465445665", 20530D, Instant.now(), 0)
        ));
        PageRequest pageRequest = PageRequest.of(0, clientes.size());
        Page<Client> page = new PageImpl<>(clientes);

        Mockito.when(repository.findAll(pageRequest)).thenReturn(page);
        Page<ClientDTO> resultado = service.findAllPaged(pageRequest);

        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(clientes.size(), resultado.getSize());
        Assertions.assertEquals(clientes.get(0).getId(), resultado.getContent().get(0).getId());
        Assertions.assertEquals(clientes.get(1).getId(), resultado.getContent().get(1).getId());
        Assertions.assertEquals(clientes.get(2).getId(), resultado.getContent().get(2).getId());
        Mockito.verify(repository , times(1)).findAll(pageRequest);
    }

    @Test
    @DisplayName("Testa metodo findByIncome que deve retornar uma pagina com os clientes que tenham o income informado (e chamar o método findByIncome do repository)")
    public void testeFindByIncomeGreaterThan(){
        List<Client> clientes = new ArrayList<>(Arrays.asList(
                new Client(2L,"Ryann", "7564345543", 20380D, Instant.now(), 1),
                new Client(3L,"Marcos", "007", 7530D, Instant.now(), 0)
        ));
        PageRequest pageRequest = PageRequest.of(0, clientes.size());
        Page<Client> page = new PageImpl<>(clientes);
        int tamanhoEsperado = 2;

        Mockito.when(repository.findByIncomeGreaterThan(1500D, pageRequest)).thenReturn(page);
        Page<ClientDTO> resultado = service.findByIncomeGreaterThan(pageRequest,1500D);

        Assertions.assertEquals(tamanhoEsperado, resultado.getContent().size());
        Assertions.assertTrue(resultado.getContent().get(0).getIncome()>1500);
        Assertions.assertTrue(resultado.getContent().get(1).getIncome()>1500);
        Mockito.verify(repository , times(1))
                .findByIncomeGreaterThan(1500D, pageRequest);
    }


    @Test
    @DisplayName("Testa metodo findById quando id existir")
    public void testeFindByIdExistente() {
        Long id = 1L;
        Client client = new Client(id,"Cayke Nakazone", "321321332", 5670D, Instant.now(),1);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(client));
        ClientDTO resultado = service.findById(id);

        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(id, resultado.getId());
        Mockito.verify(repository , times(1)).findById(id);
    }


    @Test
    @DisplayName("Testa metodo findById quando id for Inexistente e lançar ResourceNotFoundException quando o id não existir")
    public void testeFindByIdInexistente() {
        Long id = 13213L;

        Mockito.doThrow(ResourceNotFoundException.class).when(repository).findById(id);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(id));
        Mockito.verify(repository , times(1)).findById(id);
    }

    @Test
    @DisplayName("Testa metodo update quando id for Existente")
    public void testeUpdateIdExistente() {
        Long id = 1L;
        Client client = new Client(id,"Fulano da Silva", "123", 2000D, Instant.now(),1);
        ClientDTO clientDto = new ClientDTO(id,"Fulano da Silva de souza", "123", 2500D, Instant.now(),2);

        Mockito.when(repository.getOne(id)).thenReturn(client);
        Mockito.when(repository.save(client)).thenReturn(client);

        //clientDto ira atualizar o client cujo id é o mesmo
        ClientDTO resultado = service.update(id, clientDto);

        Assertions.assertEquals(ClientDTO.class, resultado.getClass());
        Assertions.assertEquals(clientDto.getId(), resultado.getId());
        Assertions.assertEquals(clientDto.getName(), resultado.getName());
        Assertions.assertEquals(clientDto.getIncome(), resultado.getIncome());
        Mockito.verify(repository , times(1)).getOne(id);
        Mockito.verify(repository , times(1)).save(client);
    }

    @Test
    @DisplayName("Testa metodo update quando id for Inexistente")
    public void testeUpdateIdInexistente() {
        Long id = 1121212L;

        Mockito.doThrow(ResourceNotFoundException.class).when(repository).getOne(id);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(id, new ClientDTO()));
        Mockito.verify(repository , times(1)).getOne(id);
    }

    @Test
    @DisplayName("Testa metodo insert que deve retornar um ClientDTO ao inserir um novo client e lançar uma ResourceNotFoundException quando o id não existir")
    public void updateDeveriaLancarExceptionQuandoIdNaoExistir() {
        long idNaoExistente = 100;
        ClientDTO dto = new ClientDTO();

        Mockito.when(repository.getOne(Mockito.anyLong())).thenThrow(new EntityNotFoundException());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(idNaoExistente, dto));
        Mockito.verify(repository, Mockito.times(1)).getOne(idNaoExistente);
    }

    @Test
    @DisplayName("Testa metodo insert que deve retornar um ClientDTO ao inserir um novo cliente")
    public void testeInsert(){
        ClientDTO clientASerInserido  = new ClientDTO(39L,"Carlos da Silva", "42344", 2000D, Instant.now(),1);
        Client client = clientASerInserido.toEntity();

        Mockito.when(repository.save(client)).thenReturn(client);
        ClientDTO resultado = service.insert(clientASerInserido);

        Assertions.assertEquals(ClientDTO.class, resultado.getClass());
        Assertions.assertEquals(clientASerInserido.getId(), resultado.getId());
        Mockito.verify(repository , times(1)).save(client);
    }



}
