package siacoes;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Before;
import org.junit.jupiter.api.Test;

import br.edu.utfpr.dv.siacoes.model.Department;

class DepartmentTest {
	
	private final Department depart = new Department();
	
	//O método @Before é executado antes do método Test, aqui está sendo criado um nome para poder dar continuidade no Test.
	@Before
	void testando() {
		depart.setName("Computacao");
	}

	@Test
	void test01() {
		assertEquals("Computacao", depart.getName());
	}
	
}
