/*------------------------------------------------------------------------------
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *----------------------------------------------------------------------------*/
package org.springside.modules.nosql.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

/**
 * JedisTemplate is the template to execute jedis actions, handle the connection with the pool correctly.
 */
public class JedisTemplate {
    private static Logger logger = LoggerFactory.getLogger(JedisTemplate.class);

    private JedisPool jedisPool;

    public JedisTemplate(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    /**
     * Execute with a call back action with result.
     */
    public <T> T execute(JedisAction<T> jedisAction) throws JedisException {
        Jedis jedis = null;
        boolean broken = false;
        try {
            jedis = jedisPool.getResource();
            return jedisAction.action(jedis);
        } catch (JedisConnectionException e) {
            logger.error("Redis connection lost.", e);
            broken = true;
            throw e;
        } finally {
            closeResource(jedis, broken);
        }
    }

    /**
     * Execute with a call back action without result.
     */
    public void execute(JedisActionNoResult jedisAction) throws JedisException {
        Jedis jedis = null;
        boolean broken = false;
        try {
            jedis = jedisPool.getResource();
            jedisAction.action(jedis);
        } catch (JedisConnectionException e) {
            logger.error("Redis connection lost.", e);
            broken = true;
            throw e;
        } finally {
            closeResource(jedis, broken);
        }
    }

    /**
     * Return jedis connection to the pool, call different return methods depends on the conectionBroken status.
     */
    protected void closeResource(Jedis jedis, boolean connectionBroken) {
        if (jedis != null) {
            if (connectionBroken) {
                jedisPool.returnBrokenResource(jedis);
            } else {
                jedisPool.returnResource(jedis);
            }
        }
    }

    /**
     * Get the internal JedisPool.
     */
    public JedisPool getJedisPool() {
        return jedisPool;
    }

    /**
     * Callback interface for template method.
     */
    public interface JedisAction<T> {
        T action(Jedis jedis);
    }

    /**
     * Callback interface for template method without result.
     */
    public interface JedisActionNoResult {
        void action(Jedis jedis);
    }
}
