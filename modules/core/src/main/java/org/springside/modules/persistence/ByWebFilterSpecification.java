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
					SingularAttribute<? super T, ?> attr = entityManager.getMetamodel().entity(clazz)
							.getSingularAttribute(filter.fieldName);
					switch (filter.operator) {
					case EQ:
						predicates.add(builder.equal(root.get(attr), filter.value));
						break;
					case LIKE:
						predicates.add(builder.like((Expression<String>) root.get(attr), "%" + filter.value + "%"));
						break;
					case GT:
						predicates.add(builder.greaterThan((Expression<Comparable>) root.get(attr),
								(Comparable) filter.value));
						break;
					case LT:
						predicates.add(builder.lessThan((Expression<Comparable>) root.get(attr),
								(Comparable) filter.value));
						break;
					case GTE:
						predicates.add(builder.greaterThanOrEqualTo((Expression<Comparable>) root.get(attr),
								(Comparable) filter.value));
						break;
					case LTE:
						predicates.add(builder.lessThanOrEqualTo((Expression<Comparable>) root.get(attr),
								(Comparable) filter.value));
						break;
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
