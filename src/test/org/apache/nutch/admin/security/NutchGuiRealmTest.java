package org.apache.nutch.admin.security;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.nutch.admin.security.NutchGuiPrincipal.KnownPrincipal;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.UserRealm;

public class NutchGuiRealmTest extends TestCase {

  @Mock
  private HttpRequest _request;

  @Override
  protected void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  public void testReauthenticate() throws Exception {
    UserRealm realm = new NutchGuiRealm();
    Principal principal = new NutchGuiPrincipal.KnownPrincipal("user",
            "password", null);
    boolean reauthenticate = realm.reauthenticate(principal);
    assertTrue(reauthenticate);
  }

  public void testPushRole() throws Exception {
    UserRealm realm = new NutchGuiRealm();
    Principal principal = new NutchGuiPrincipal.KnownPrincipal("user",
            "password", null);
    Principal principal2 = realm.pushRole(principal, "role");
    assertEquals(principal, principal2);
  }

  public void testPopRole() throws Exception {
    UserRealm realm = new NutchGuiRealm();
    Principal principal = new NutchGuiPrincipal.KnownPrincipal("user",
            "password", null);
    Principal principal2 = realm.popRole(principal);
    assertEquals(principal, principal2);
  }

  public void testLogout() throws Exception {
    UserRealm realm = new NutchGuiRealm();
    Principal principal = new NutchGuiPrincipal.KnownPrincipal("user",
            "password", null);
    realm.logout(principal);
  }

  public void testIsUserInRole() throws Exception {
    UserRealm realm = new NutchGuiRealm();
    Set<String> set = new HashSet<String>();
    set.add("role");
    Principal principal = new NutchGuiPrincipal.KnownPrincipal("user",
            "password", set);
    assertTrue(realm.isUserInRole(principal, "role"));
    assertFalse(realm.isUserInRole(principal, "foo"));
  }


  public void testGetPrincipal() throws Exception {
    UserRealm realm = new NutchGuiRealm();
    try {
      realm.getPrincipal(null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testGetName() throws Exception {
    UserRealm realm = new NutchGuiRealm();
    try {
      realm.getName();
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testDisassociate() throws Exception {
    UserRealm realm = new NutchGuiRealm();
    realm.disassociate(null);
  }

  public void testAuthenticate() throws Exception {
    UserRealm realm = new NutchGuiRealm();
    Mockito.when(_request.getParameter("j_username")).thenReturn("foo");
    Mockito.when(_request.getParameter("j_password")).thenReturn("bar");

    Principal principal = realm.authenticate(null, null, _request);
    assertTrue(principal instanceof KnownPrincipal);
    KnownPrincipal knownPrincipal = (KnownPrincipal) principal;
    assertEquals("foo", knownPrincipal.getName());
  }

  public void testIsUserInRoleAfterLogin() throws Exception {
    UserRealm realm = new NutchGuiRealm();
    Mockito.when(_request.getParameter("j_username")).thenReturn("foo");
    Mockito.when(_request.getParameter("j_password")).thenReturn("bar");

    Principal principal = realm.authenticate(null, null, _request);
    assertTrue(realm.isUserInRole(principal, "user"));
    assertTrue(realm.isUserInRole(principal, "admin"));
    assertFalse(realm.isUserInRole(principal, "foobar"));
  }

}