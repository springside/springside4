package org.springside.examples.showcase.common;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springside.modules.test.data.Fixtures;
import org.springside.modules.test.spring.SpringTxTestCase;

@ContextConfiguration(locations = { "/applicationContext.xml" })
@TransactionConfiguration(transactionManager = "defaultTransactionManager")
public class JpaMappingTest extends SpringTxTestCase {

	private static Logger logger = LoggerFactory.getLogger(JpaMappingTest.class);

	@PersistenceContext
	private EntityManager em;

	@Test
	public void allClassMapping() throws Exception {
		Fixtures.reloadData(dataSource, "/data/sample-data.xml");
		Metamodel model = em.getEntityManagerFactory().getMetamodel();
		for (EntityType entityType : model.getEntities()) {
			String entityName = entityType.getName();
			em.createQuery("select o from " + entityName + " o").getFirstResult();
			logger.info("ok: " + entityName);
		}
	}
}
