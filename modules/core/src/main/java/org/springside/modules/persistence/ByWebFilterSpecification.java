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
						Class gtc = attr.getJavaType();
						predicates.add(createGreaterPredicate(root, builder, attr, filter.value, gtc));
						break;
					case LT:
						Class ltc = attr.getJavaType();
						predicates.add(createLessPredicate(root, builder, attr, filter.value, ltc));
						break;
					case GTE:
						Class gtec = attr.getJavaType();
						predicates.add(createGreaterOrEqualPredicate(root, builder, attr, filter.value, gtec));
						break;
					case LTE:
						Class ltec = attr.getJavaType();
						predicates.add(createLessOrEqualPredicate(root, builder, attr, filter.value, ltec));
						break;
					}
				}

				if (predicates.size() > 0) {
					return builder.and(predicates.toArray(new Predicate[predicates.size()]));
				}

				return builder.conjunction();
			}

			public <Y extends Comparable<? super Y>> Predicate createGreaterPredicate(Root<T> root,
					CriteriaBuilder builder, SingularAttribute<? super T, ?> attr, Object value, Class<Y> clazz) {
				return builder.greaterThan((Expression<Y>) root.get(attr), (Y) value);
			}

			public <Y extends Comparable<? super Y>> Predicate createGreaterOrEqualPredicate(Root<T> root,
					CriteriaBuilder builder, SingularAttribute<? super T, ?> attr, Object value, Class<Y> clazz) {
				return builder.greaterThanOrEqualTo((Expression<Y>) root.get(attr), (Y) value);
			}

			public <Y extends Comparable<? super Y>> Predicate createLessPredicate(Root<T> root,
					CriteriaBuilder builder, SingularAttribute<? super T, ?> attr, Object value, Class<Y> clazz) {
				return builder.lessThan((Expression<Y>) root.get(attr), (Y) value);
			}

			public <Y extends Comparable<? super Y>> Predicate createLessOrEqualPredicate(Root<T> root,
					CriteriaBuilder builder, SingularAttribute<? super T, ?> attr, Object value, Class<Y> clazz) {
				return builder.lessThanOrEqualTo((Expression<Y>) root.get(attr), (Y) value);
			}
		};
	}
}
