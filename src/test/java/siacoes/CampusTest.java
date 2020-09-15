package siacoes;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Before;
import org.junit.jupiter.api.Test;

import br.edu.utfpr.dv.siacoes.model.Campus;

class CampusTest {

	private final Campus campus = new Campus();
	
	//O método @Before é executado antes do método Test, aqui está sendo criado um nome para poder dar continuidade no Test.
	@Before
	void testando() {
		campus.setAddress("Cornelio Procopio");
	}
	
	@Test
	void test() {
		assertEquals("Cornelio Procopio", campus.getAddress());
	}

}
