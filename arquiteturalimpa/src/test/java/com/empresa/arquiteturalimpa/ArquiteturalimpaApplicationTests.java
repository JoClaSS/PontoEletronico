package com.empresa.arquiteturalimpa;

import org.junit.jupiter.api.Test;

// Sem @SpringBootTest: subir o contexto completo exige um Postgres real acessível
// via .env (DB_URL/JWT_SECRET etc.), assim como no mvcpontoeletronico, que não possui testes de contexto.
class ArquiteturalimpaApplicationTests {

	@Test
	void contextLoads() {
	}

}
