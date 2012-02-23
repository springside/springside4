package org.springside.examples.showcase.security;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

public class FormAuthenticationWithLockFilter extends FormAuthenticationFilter {

    private long                                        maxLoginAttempts = 10;

    public static ConcurrentHashMap<String, AtomicLong> accountLockMap   = new ConcurrentHashMap<String, AtomicLong>();

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        AuthenticationToken token = createToken(request, response);

        if (token == null) {
            String msg = "createToken method implementation returned null. A valid non-null AuthenticationToken "
                    + "must be created in order to execute a login attempt.";
            throw new IllegalStateException(msg);
        }

        if (checkIfAccountLocked(request)) {
            return onLoginFailure(token, new ExcessiveAttemptsException(), request, response);
        } else {
            if (!doLogin(request, response, token)) {
                resetAccountLock(getUsername(request));
                return false;
            }
            return true;
        }
    }

    private boolean checkIfAccountLocked(ServletRequest request) {
        String username = getUsername(request);
        if (accountLockMap.get((String) username) != null) {
            long remainLoginAttempts = accountLockMap.get((String) username).get();
            if (remainLoginAttempts <= 0) {
                return true;
            }
        }
        return false;
    }

    private boolean doLogin(ServletRequest request, ServletResponse response, AuthenticationToken token)
            throws Exception {
        try {
            Subject subject = getSubject(request, response);
            subject.login(token);

            return onLoginSuccess(token, subject, request, response);
        } catch (IncorrectCredentialsException e) {
            decreaseAccountLoginAttempts(request);
            checkIfAccountLocked(request);

            return onLoginFailure(token, e, request, response);
        } catch (AuthenticationException e) {
            return onLoginFailure(token, e, request, response);
        }
    }

    private void decreaseAccountLoginAttempts(ServletRequest request) {
        AtomicLong initValue = new AtomicLong(maxLoginAttempts);
        AtomicLong remainLoginAttempts = accountLockMap.putIfAbsent(getUsername(request), new AtomicLong(maxLoginAttempts));
        if (remainLoginAttempts == null) {
            remainLoginAttempts = initValue;
        }
        remainLoginAttempts.getAndDecrement();
        accountLockMap.put(getUsername(request), remainLoginAttempts);
    }

    private void resetAccountLock(String username) {
        accountLockMap.put(username, new AtomicLong(maxLoginAttempts));
    }

    public void setMaxLoginAttempts(long maxLoginAttempts) {
        this.maxLoginAttempts = maxLoginAttempts;
    }
}
