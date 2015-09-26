/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2015 All Rights Reserved.
 */
package org.springside.examples.quickstart.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;
import org.springside.examples.quickstart.entity.User;
import org.springside.modules.persistence.DynamicSpecifications;
import org.springside.modules.persistence.SearchFilter;
import org.springside.modules.test.spring.SpringTransactionalTestCase;
import org.springside.modules.utils.Clock;

/**
 * userDao测试
 * @author Fei
 * @version $Id: UserDaoTest.java, v 0.1 2015年9月26日 下午8:48:20 Fei Exp $
 */
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class UserDaoTest extends SpringTransactionalTestCase {

    @Autowired
    private UserDao userDao;

    @Test
    public void searchRegisterDate() throws Exception {
        Map<String, Object> searchParams = new HashMap<String, Object>();
        searchParams.put("EQ_name", "Calvin");

        org.springside.modules.utils.Clock.MockClock clock = new Clock.MockClock();
        Date start = clock.getCurrentDate();
        clock.update(new Date());
        Date end = clock.getCurrentDate();

        searchParams.put("BTW_registerDate_start", start);
        searchParams.put("BTW_registerDate_end", end);
        Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);

        Specification<User> spec = DynamicSpecifications.bySearchFilter(filters.values(),
            User.class);
        List<User> userList = userDao.findAll(spec);
        assertThat(userList).hasSize(1);
        assertThat(userList.get(0).getName()).isEqualTo("Calvin");

    }
}
