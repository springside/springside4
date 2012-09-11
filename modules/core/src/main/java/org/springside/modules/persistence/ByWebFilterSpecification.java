package org.springside.modules.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.springframework.data.jpa.domain.Specification;
import org.springside.modules.persistence.SearchFilter.Operator;

import com.google.common.collect.Lists;

public class ByWebFilterSpecification {
	@PersistenceContext
	private EntityManager entityManager;

	public <T> Specification<T> byWebFilter(final List<SearchFilter> filters, final Class<T> clazz) {
		return new Specification<T>() {
			@Override
			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = Lists.newArrayList();
				for (SearchFilter filter : filters) {
					SingularAttribute<T, ?> attr = entityManager.getMetamodel().entity(clazz)
							.getDeclaredSingularAttribute(filter.fieldName);
					if (filter.operator.equals(Operator.EQ)) {
						predicates.add(builder.equal(root.get(attr), filter.value));
					}
					if (filter.operator.equals(Operator.LIKE)) {
						predicates.add(builder.like((Expression<String>) root.get(attr), "%" + filter.value + "%"));
					}

				}

				if (predicates.size() > 0) {
					return builder.and(predicates.toArray(new Predicate[predicates.size()]));
				}

				return builder.conjunction();
			}
		};
	}
}
