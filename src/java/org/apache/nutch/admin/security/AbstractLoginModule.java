package org.apache.nutch.admin.security;

import java.security.Principal;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.nutch.admin.security.NutchGuiPrincipal.KnownPrincipal;

public abstract class AbstractLoginModule implements LoginModule {

  private final Log LOG = LogFactory.getLog(AbstractLoginModule.class);

  private Subject _subject;
  private CallbackHandler _callbackHandler;
  private boolean _authenticated;

  private KnownPrincipal _currentPrincipal;

  private boolean _committed;

  @Override
  public boolean abort() throws LoginException {
    _currentPrincipal = null;
    return (isAuthenticated() && isCommitted());
  }

  @Override
  public boolean commit() throws LoginException {
    if (!isAuthenticated()) {
      _currentPrincipal = null;
      setCommitted(false);
    } else {
      Set<Principal> principals = _subject.getPrincipals();
      principals.add(_currentPrincipal);
      setCommitted(true);
    }
    return isCommitted();
  }

  private void setCommitted(boolean committed) {
    _committed = committed;
  }

  private boolean isCommitted() {
    return _committed;
  }

  @Override
  public void initialize(Subject subject, CallbackHandler callbackHandler,
          Map<String, ?> sharedState, Map<String, ?> options) {
    _subject = subject;
    _callbackHandler = callbackHandler;
  }

  @Override
  public boolean login() throws LoginException {
    NameCallback nameCallback = new NameCallback("user name:");
    PasswordCallback passwordCallback = new PasswordCallback("password:", false);
    try {
      _callbackHandler
              .handle(new Callback[] { nameCallback, passwordCallback });
      String name = nameCallback.getName();
      char[] password = passwordCallback.getPassword();

      if (name != null && name.length() > 0) {
        KnownPrincipal knownPrincipal = getKnownPrincipal(name);
        if (knownPrincipal != null) {
          if (password != null && password.length > 0) {
            String knownPassword = knownPrincipal.getPassword();
            if (knownPassword.equals(new String(password))) {
              setAuthenticated(true);
              _currentPrincipal = knownPrincipal;
            }
          }
        }
      }
    } catch (Exception e) {
      LOG.error("login failed.", e);
      throw new LoginException(e.getMessage());
    }
    return isAuthenticated();
  }

  private boolean isAuthenticated() {
    return _authenticated;
  }

  private void setAuthenticated(boolean authenticated) {
    _authenticated = authenticated;
  }

  @Override
  public boolean logout() throws LoginException {
    Set<Principal> principals = _subject.getPrincipals();
    principals.remove(_currentPrincipal);
    return true;
  }

  protected abstract KnownPrincipal getKnownPrincipal(String userName);
}