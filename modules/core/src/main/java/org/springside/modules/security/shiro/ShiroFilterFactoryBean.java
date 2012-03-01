package org.springside.modules.security.shiro;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.config.Ini;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.Nameable;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.config.IniFilterChainResolverFactory;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.filter.authc.AuthenticationFilter;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * {@link org.springframework.beans.factory.FactoryBean FactoryBean} to be used in Spring-based web applications for
 * defining the master Shiro Filter.
 * <h4>Usage</h4>
 * Declare a DelegatingFilterProxy in {@code web.xml}, matching the filter name to the bean id:
 * <pre>
 * &lt;filter&gt;
 *   &lt;filter-name&gt;<b>shiroFilter</b>&lt;/filter-name&gt;
 *   &lt;filter-class&gt;org.springframework.web.filter.DelegatingFilterProxy&lt;filter-class&gt;
 *   &lt;init-param&gt;
 *    &lt;param-name&gt;targetFilterLifecycle&lt;/param-name&gt;
 *     &lt;param-value&gt;true&lt;/param-value&gt;
 *   &lt;/init-param&gt;
 * &lt;/filter&gt;
 * </pre>
 * Then, in your spring XML file that defines your web ApplicationContext:
 * <pre>
 * &lt;bean id="<b>shiroFilter</b>" class="org.apache.shiro.spring.web.ShiroFilterFactoryBean"&gt;
 *    &lt;property name="securityManager" ref="securityManager"/&gt;
 *    &lt;!-- other properties as necessary ... --&gt;
 * &lt;/bean&gt;
 * </pre>
 * <h4>Filter Auto-Discovery</h4>
 * While there is a {@link #setFilters(java.util.Map) filters} property that allows you to assign a filter beans
 * to the 'pool' of filters available when defining {@link #setFilterChainDefinitions(String) filter chains}, it is
 * optional.
 * <p/>
 * This implementation is also a {@link BeanPostProcessor} and will acquire
 * any {@link javax.servlet.Filter Filter} beans defined independently in your Spring application context.  Upon
 * discovery, they will be automatically added to the {@link #setFilters(java.util.Map) map} keyed by the bean ID.
 * That ID can then be used in the filter chain definitions, for example:
 *
 * <pre>
 * &lt;bean id="<b>myCustomFilter</b>" class="com.class.that.implements.javax.servlet.Filter"/&gt;
 * ...
 * &lt;bean id="shiroFilter" class="org.apache.shiro.spring.web.ShiroFilterFactoryBean"&gt;
 *    ...
 *    &lt;property name="filterChainDefinitions"&gt;
 *        &lt;value&gt;
 *            /some/path/** = authc, <b>myCustomFilter</b>
 *        &lt;/value&gt;
 *    &lt;/property&gt;
 * &lt;/bean&gt;
 * </pre>
 * <h4>Global Property Values</h4>
 * Most Shiro servlet Filter implementations exist for defining custom Filter
 * {@link #setFilterChainDefinitions(String) chain definitions}.  Most implementations subclass one of the
 * {@link AccessControlFilter}, {@link AuthenticationFilter}, {@link AuthorizationFilter} classes to simplify things,
 * and each of these 3 classes has configurable properties that are application-specific.
 * <p/>
 * A dilemma arises where, if you want to for example set the application's 'loginUrl' for any Filter, you don't want
 * to have to manually specify that value for <em>each</em> filter instance definied.
 * <p/>
 * To prevent configuration duplication, this implementation provides the following properties to allow you
 * to set relevant values in only one place:
 * <ul>
 * <li>{@link #setLoginUrl(String)}</li>
 * <li>{@link #setSuccessUrl(String)}</li>
 * <li>{@link #setUnauthorizedUrl(String)}</li>
 * </ul>
 *
 * Then at startup, any values specified via these 3 properties will be applied to all configured
 * Filter instances so you don't have to specify them individually on each filter instance.  To ensure your own custom
 * filters benefit from this convenience, your filter implementation should subclass one of the 3 mentioned
 * earlier.
 *
 * @see org.springframework.web.filter.DelegatingFilterProxy DelegatingFilterProxy
 * @since 1.0
 */
//因为与Spring JPA Data同时使用时，ShiroFilterFacotryBean的BeanPostProcessor报找不到XXDao类，因此暂时用取消BeanPostProcessor定义的方法规避
public class ShiroFilterFactoryBean implements FactoryBean {

	private static transient final Logger log = LoggerFactory.getLogger(ShiroFilterFactoryBean.class);

	private SecurityManager securityManager;

	private Map<String, Filter> filters;

	private Map<String, String> filterChainDefinitionMap; //urlPathExpression_to_comma-delimited-filter-chain-definition

	private String loginUrl;
	private String successUrl;
	private String unauthorizedUrl;

	private AbstractShiroFilter instance;

	public ShiroFilterFactoryBean() {
		this.filters = new LinkedHashMap<String, Filter>();
		this.filterChainDefinitionMap = new LinkedHashMap<String, String>(); //order matters!
	}

	/**
	 * Sets the application {@code SecurityManager} instance to be used by the constructed Shiro Filter.  This is a
	 * required property - failure to set it will throw an initialization exception.
	 *
	 * @return the application {@code SecurityManager} instance to be used by the constructed Shiro Filter.
	 */
	public SecurityManager getSecurityManager() {
		return securityManager;
	}

	/**
	 * Sets the application {@code SecurityManager} instance to be used by the constructed Shiro Filter.  This is a
	 * required property - failure to set it will throw an initialization exception.
	 *
	 * @param securityManager the application {@code SecurityManager} instance to be used by the constructed Shiro Filter.
	 */
	public void setSecurityManager(SecurityManager securityManager) {
		this.securityManager = securityManager;
	}

	/**
	 * Returns the application's login URL to be assigned to all acquired Filters that subclass
	 * {@link AccessControlFilter} or {@code null} if no value should be assigned globally. The default value
	 * is {@code null}.
	 *
	 * @return the application's login URL to be assigned to all acquired Filters that subclass
	 *         {@link AccessControlFilter} or {@code null} if no value should be assigned globally.
	 * @see #setLoginUrl
	 */
	public String getLoginUrl() {
		return loginUrl;
	}

	/**
	 * Sets the application's login URL to be assigned to all acquired Filters that subclass
	 * {@link AccessControlFilter}.  This is a convenience mechanism: for all configured {@link #setFilters filters},
	 * as well for any default ones ({@code authc}, {@code user}, etc), this value will be passed on to each Filter
	 * via the {@link AccessControlFilter#setLoginUrl(String)} method<b>*</b>.  This eliminates the need to
	 * configure the 'loginUrl' property manually on each filter instance, and instead that can be configured once
	 * via this attribute.
	 * <p/>
	 * <b>*</b>If a filter already has already been explicitly configured with a value, it will
	 * <em>not</em> receive this value. Individual filter configuration overrides this global convenience property.
	 *
	 * @param loginUrl the application's login URL to apply to as a convenience to all discovered
	 *                 {@link AccessControlFilter} instances.
	 * @see AccessControlFilter#setLoginUrl(String)
	 */
	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	/**
	 * Returns the application's after-login success URL to be assigned to all acquired Filters that subclass
	 * {@link AuthenticationFilter} or {@code null} if no value should be assigned globally. The default value
	 * is {@code null}.
	 *
	 * @return the application's after-login success URL to be assigned to all acquired Filters that subclass
	 *         {@link AuthenticationFilter} or {@code null} if no value should be assigned globally.
	 * @see #setSuccessUrl
	 */
	public String getSuccessUrl() {
		return successUrl;
	}

	/**
	 * Sets the application's after-login success URL to be assigned to all acquired Filters that subclass
	 * {@link AuthenticationFilter}.  This is a convenience mechanism: for all configured {@link #setFilters filters},
	 * as well for any default ones ({@code authc}, {@code user}, etc), this value will be passed on to each Filter
	 * via the {@link AuthenticationFilter#setSuccessUrl(String)} method<b>*</b>.  This eliminates the need to
	 * configure the 'successUrl' property manually on each filter instance, and instead that can be configured once
	 * via this attribute.
	 * <p/>
	 * <b>*</b>If a filter already has already been explicitly configured with a value, it will
	 * <em>not</em> receive this value. Individual filter configuration overrides this global convenience property.
	 *
	 * @param successUrl the application's after-login success URL to apply to as a convenience to all discovered
	 *                   {@link AccessControlFilter} instances.
	 * @see AuthenticationFilter#setSuccessUrl(String)
	 */
	public void setSuccessUrl(String successUrl) {
		this.successUrl = successUrl;
	}

	/**
	 * Returns the application's after-login success URL to be assigned to all acquired Filters that subclass
	 * {@link AuthenticationFilter} or {@code null} if no value should be assigned globally. The default value
	 * is {@code null}.
	 *
	 * @return the application's after-login success URL to be assigned to all acquired Filters that subclass
	 *         {@link AuthenticationFilter} or {@code null} if no value should be assigned globally.
	 * @see #setSuccessUrl
	 */
	public String getUnauthorizedUrl() {
		return unauthorizedUrl;
	}

	/**
	 * Sets the application's 'unauthorized' URL to be assigned to all acquired Filters that subclass
	 * {@link AuthorizationFilter}.  This is a convenience mechanism: for all configured {@link #setFilters filters},
	 * as well for any default ones ({@code roles}, {@code perms}, etc), this value will be passed on to each Filter
	 * via the {@link AuthorizationFilter#setUnauthorizedUrl(String)} method<b>*</b>.  This eliminates the need to
	 * configure the 'unauthorizedUrl' property manually on each filter instance, and instead that can be configured once
	 * via this attribute.
	 * <p/>
	 * <b>*</b>If a filter already has already been explicitly configured with a value, it will
	 * <em>not</em> receive this value. Individual filter configuration overrides this global convenience property.
	 *
	 * @param unauthorizedUrl the application's 'unauthorized' URL to apply to as a convenience to all discovered
	 *                        {@link AuthorizationFilter} instances.
	 * @see AuthorizationFilter#setUnauthorizedUrl(String)
	 */
	public void setUnauthorizedUrl(String unauthorizedUrl) {
		this.unauthorizedUrl = unauthorizedUrl;
	}

	/**
	 * Returns the filterName-to-Filter map of filters available for reference when defining filter chain definitions.
	 * All filter chain definitions will reference filters by the names in this map (i.e. the keys).
	 *
	 * @return the filterName-to-Filter map of filters available for reference when defining filter chain definitions.
	 */
	public Map<String, Filter> getFilters() {
		return filters;
	}

	/**
	 * Sets the filterName-to-Filter map of filters available for reference when creating
	 * {@link #setFilterChainDefinitionMap(java.util.Map) filter chain definitions}.
	 * <p/>
	 * <b>Note:</b> This property is optional:  this {@code FactoryBean} implementation will discover all beans in the
	 * web application context that implement the {@link Filter} interface and automatically add them to this filter
	 * map under their bean name.
	 * <p/>
	 * For example, just defining this bean in a web Spring XML application context:
	 * <pre>
	 * &lt;bean id=&quot;myFilter&quot; class=&quot;com.class.that.implements.javax.servlet.Filter&quot;&gt;
	 * ...
	 * &lt;/bean&gt;</pre>
	 * Will automatically place that bean into this Filters map under the key '<b>myFilter</b>'.
	 *
	 * @param filters the optional filterName-to-Filter map of filters available for reference when creating
	 *                {@link #setFilterChainDefinitionMap (java.util.Map) filter chain definitions}.
	 */
	public void setFilters(Map<String, Filter> filters) {
		this.filters = filters;
	}

	/**
	 * Returns the chainName-to-chainDefinition map of chain definitions to use for creating filter chains intercepted
	 * by the Shiro Filter.  Each map entry should conform to the format defined by the
	 * {@link FilterChainManager#createChain(String, String)} JavaDoc, where the map key is the chain name (e.g. URL
	 * path expression) and the map value is the comma-delimited string chain definition.
	 *
	 * @return he chainName-to-chainDefinition map of chain definitions to use for creating filter chains intercepted
	 *         by the Shiro Filter.
	 */
	public Map<String, String> getFilterChainDefinitionMap() {
		return filterChainDefinitionMap;
	}

	/**
	 * Sets the chainName-to-chainDefinition map of chain definitions to use for creating filter chains intercepted
	 * by the Shiro Filter.  Each map entry should conform to the format defined by the
	 * {@link FilterChainManager#createChain(String, String)} JavaDoc, where the map key is the chain name (e.g. URL
	 * path expression) and the map value is the comma-delimited string chain definition.
	 *
	 * @param filterChainDefinitionMap the chainName-to-chainDefinition map of chain definitions to use for creating
	 *                                 filter chains intercepted by the Shiro Filter.
	 */
	public void setFilterChainDefinitionMap(Map<String, String> filterChainDefinitionMap) {
		this.filterChainDefinitionMap = filterChainDefinitionMap;
	}

	/**
	 * A convenience method that sets the {@link #setFilterChainDefinitionMap(java.util.Map) filterChainDefinitionMap}
	 * property by accepting a {@link java.util.Properties Properties}-compatible string (multi-line key/value pairs).
	 * Each key/value pair must conform to the format defined by the
	 * {@link FilterChainManager#createChain(String,String)} JavaDoc - each property key is an ant URL
	 * path expression and the value is the comma-delimited chain definition.
	 *
	 * @param definitions a {@link java.util.Properties Properties}-compatible string (multi-line key/value pairs)
	 *                    where each key/value pair represents a single urlPathExpression-commaDelimitedChainDefinition.
	 */
	public void setFilterChainDefinitions(String definitions) {
		Ini ini = new Ini();
		ini.load(definitions);
		//did they explicitly state a 'urls' section?  Not necessary, but just in case:
		Ini.Section section = ini.getSection(IniFilterChainResolverFactory.URLS);
		if (CollectionUtils.isEmpty(section)) {
			//no urls section.  Since this _is_ a urls chain definition property, just assume the
			//default section contains only the definitions:
			section = ini.getSection(Ini.DEFAULT_SECTION_NAME);
		}
		setFilterChainDefinitionMap(section);
	}

	/**
	 * Lazily creates and returns a {@link AbstractShiroFilter} concrete instance via the
	 * {@link #createInstance} method.
	 *
	 * @return the application's Shiro Filter instance used to filter incoming web requests.
	 * @throws Exception if there is a problem creating the {@code Filter} instance.
	 */
	public Object getObject() throws Exception {
		if (instance == null) {
			instance = createInstance();
		}
		return instance;
	}

	/**
	 * Returns <code>{@link org.apache.shiro.web.servlet.AbstractShiroFilter}.class</code>
	 *
	 * @return <code>{@link org.apache.shiro.web.servlet.AbstractShiroFilter}.class</code>
	 */
	public Class getObjectType() {
		return SpringShiroFilter.class;
	}

	/**
	 * Returns {@code true} always.  There is almost always only ever 1 Shiro {@code Filter} per web application.
	 *
	 * @return {@code true} always.  There is almost always only ever 1 Shiro {@code Filter} per web application.
	 */
	public boolean isSingleton() {
		return true;
	}

	protected FilterChainManager createFilterChainManager() {

		DefaultFilterChainManager manager = new DefaultFilterChainManager();
		Map<String, Filter> defaultFilters = manager.getFilters();
		//apply global settings if necessary:
		for (Filter filter : defaultFilters.values()) {
			applyGlobalPropertiesIfNecessary(filter);
		}

		//Apply the acquired and/or configured filters:
		Map<String, Filter> filters = getFilters();
		if (!CollectionUtils.isEmpty(filters)) {
			for (Map.Entry<String, Filter> entry : filters.entrySet()) {
				String name = entry.getKey();
				Filter filter = entry.getValue();
				applyGlobalPropertiesIfNecessary(filter);
				if (filter instanceof Nameable) {
					((Nameable) filter).setName(name);
				}
				//'init' argument is false, since Spring-configured filters should be initialized
				//in Spring (i.e. 'init-method=blah') or implement InitializingBean:
				manager.addFilter(name, filter, false);
			}
		}

		//build up the chains:
		Map<String, String> chains = getFilterChainDefinitionMap();
		if (!CollectionUtils.isEmpty(chains)) {
			for (Map.Entry<String, String> entry : chains.entrySet()) {
				String url = entry.getKey();
				String chainDefinition = entry.getValue();
				manager.createChain(url, chainDefinition);
			}
		}

		return manager;
	}

	/**
	 * This implementation:
	 * <ol>
	 * <li>Ensures the required {@link #setSecurityManager(org.apache.shiro.mgt.SecurityManager) securityManager}
	 * property has been set</li>
	 * <li>{@link #createFilterChainManager() Creates} a {@link FilterChainManager} instance that reflects the
	 * configured {@link #setFilters(java.util.Map) filters} and
	 * {@link #setFilterChainDefinitionMap(java.util.Map) filter chain definitions}</li>
	 * <li>Wraps the FilterChainManager with a suitable
	 * {@link org.apache.shiro.web.filter.mgt.FilterChainResolver FilterChainResolver} since the Shiro Filter
	 * implementations do not know of {@code FilterChainManager}s</li>
	 * <li>Sets both the {@code SecurityManager} and {@code FilterChainResolver} instances on a new Shiro Filter
	 * instance and returns that filter instance.</li>
	 * </ol>
	 *
	 * @return a new Shiro Filter reflecting any configured filters and filter chain definitions.
	 * @throws Exception if there is a problem creating the AbstractShiroFilter instance.
	 */
	protected AbstractShiroFilter createInstance() throws Exception {

		log.debug("Creating Shiro Filter instance.");

		SecurityManager securityManager = getSecurityManager();
		if (securityManager == null) {
			String msg = "SecurityManager property must be set.";
			throw new BeanInitializationException(msg);
		}

		if (!(securityManager instanceof WebSecurityManager)) {
			String msg = "The security manager does not implement the WebSecurityManager interface.";
			throw new BeanInitializationException(msg);
		}

		FilterChainManager manager = createFilterChainManager();

		//Expose the constructed FilterChainManager by first wrapping it in a
		// FilterChainResolver implementation. The AbstractShiroFilter implementations
		// do not know about FilterChainManagers - only resolvers:
		PathMatchingFilterChainResolver chainResolver = new PathMatchingFilterChainResolver();
		chainResolver.setFilterChainManager(manager);

		//Now create a concrete ShiroFilter instance and apply the acquired SecurityManager and built
		//FilterChainResolver.  It doesn't matter that the instance is an anonymous inner class
		//here - we're just using it because it is a concrete AbstractShiroFilter instance that accepts
		//injection of the SecurityManager and FilterChainResolver:
		return new SpringShiroFilter((WebSecurityManager) securityManager, chainResolver);
	}

	private void applyLoginUrlIfNecessary(Filter filter) {
		String loginUrl = getLoginUrl();
		if (StringUtils.hasText(loginUrl) && (filter instanceof AccessControlFilter)) {
			AccessControlFilter acFilter = (AccessControlFilter) filter;
			//only apply the login url if they haven't explicitly configured one already:
			String existingLoginUrl = acFilter.getLoginUrl();
			if (AccessControlFilter.DEFAULT_LOGIN_URL.equals(existingLoginUrl)) {
				acFilter.setLoginUrl(loginUrl);
			}
		}
	}

	private void applySuccessUrlIfNecessary(Filter filter) {
		String successUrl = getSuccessUrl();
		if (StringUtils.hasText(successUrl) && (filter instanceof AuthenticationFilter)) {
			AuthenticationFilter authcFilter = (AuthenticationFilter) filter;
			//only apply the successUrl if they haven't explicitly configured one already:
			String existingSuccessUrl = authcFilter.getSuccessUrl();
			if (AuthenticationFilter.DEFAULT_SUCCESS_URL.equals(existingSuccessUrl)) {
				authcFilter.setSuccessUrl(successUrl);
			}
		}
	}

	private void applyUnauthorizedUrlIfNecessary(Filter filter) {
		String unauthorizedUrl = getUnauthorizedUrl();
		if (StringUtils.hasText(unauthorizedUrl) && (filter instanceof AuthorizationFilter)) {
			AuthorizationFilter authzFilter = (AuthorizationFilter) filter;
			//only apply the unauthorizedUrl if they haven't explicitly configured one already:
			String existingUnauthorizedUrl = authzFilter.getUnauthorizedUrl();
			if (existingUnauthorizedUrl == null) {
				authzFilter.setUnauthorizedUrl(unauthorizedUrl);
			}
		}
	}

	private void applyGlobalPropertiesIfNecessary(Filter filter) {
		applyLoginUrlIfNecessary(filter);
		applySuccessUrlIfNecessary(filter);
		applyUnauthorizedUrlIfNecessary(filter);
	}

	/**
	 * Inspects a bean, and if it implements the {@link Filter} interface, automatically adds that filter
	 * instance to the internal {@link #setFilters(java.util.Map) filters map} that will be referenced
	 * later during filter chain construction.
	 */
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof Filter) {
			log.debug("Found filter chain candidate filter '{}'", beanName);
			Filter filter = (Filter) bean;
			applyGlobalPropertiesIfNecessary(filter);
			getFilters().put(beanName, filter);
		} else {
			log.trace("Ignoring non-Filter bean '{}'", beanName);
		}
		return bean;
	}

	/**
	 * Does nothing - only exists to satisfy the BeanPostProcessor interface and immediately returns the
	 * {@code bean} argument.
	 */
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	/**
	 * Ordinarily the {@code AbstractShiroFilter} must be subclassed to additionally perform configuration
	 * and initialization behavior.  Because this {@code FactoryBean} implementation manually builds the
	 * {@link AbstractShiroFilter}'s
	 * {@link AbstractShiroFilter#setSecurityManager(org.apache.shiro.web.mgt.WebSecurityManager) securityManager} and
	 * {@link AbstractShiroFilter#setFilterChainResolver(org.apache.shiro.web.filter.mgt.FilterChainResolver) filterChainResolver}
	 * properties, the only thing left to do is set those properties explicitly.  We do that in a simple
	 * concrete subclass in the constructor.
	 */
	private static final class SpringShiroFilter extends AbstractShiroFilter {

		protected SpringShiroFilter(WebSecurityManager webSecurityManager, FilterChainResolver resolver) {
			super();
			if (webSecurityManager == null) {
				throw new IllegalArgumentException("WebSecurityManager property cannot be null.");
			}
			setSecurityManager(webSecurityManager);
			if (resolver != null) {
				setFilterChainResolver(resolver);
			}
		}
	}
}
