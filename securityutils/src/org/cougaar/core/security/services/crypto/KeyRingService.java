/* 
 * <copyright> 
 *  Copyright 1999-2004 Cougaar Software, Inc.
 *  under sponsorship of the Defense Advanced Research Projects 
 *  Agency (DARPA). 
 *  
 *  You can redistribute this software and/or modify it under the
 *  terms of the Cougaar Open Source License as published on the
 *  Cougaar Open Source Website (www.cougaar.org).  
 *  
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 *  OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 *  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 * </copyright> 
 */ 


package org.cougaar.core.security.services.crypto;

import org.cougaar.core.component.Service;
import org.cougaar.core.security.crypto.CertificateChainException;
import org.cougaar.core.security.crypto.CertificateRevokedException;
import org.cougaar.core.security.crypto.CertificateStatus;
import org.cougaar.core.security.crypto.PrivateKeyCert;
import org.cougaar.core.security.naming.CertificateEntry;
import org.cougaar.core.security.policy.TrustedCaPolicy;
import javax.net.ssl.X509KeyManager;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Hashtable;
import java.util.List;

import sun.security.x509.X500Name;

/** Low-level service to retrieve certificates and private keys
 */
public interface KeyRingService extends Service {

  public static final int LOOKUP_LDAP               = 1;
  public static final int LOOKUP_KEYSTORE           = 2;
  public static final int LOOKUP_FORCE_LDAP_REFRESH = 4;

  void setKeyManager(X509KeyManager km);
  void finishInitialization();

  /**
   * return a private key for the given certificate or null if not found
   */
  PrivateKey findPrivateKey(X509Certificate cert);

   /**
   * @return A list of PrivateKeyCert
   */

  List findPrivateKey(String commonName);
  List findPrivateKey(String commonName, boolean validOnly);
  List findPrivateKey(X500Name x500name);

  /**
   * Check the the private key exists for the cert.  No  error
   * is generated if no  private key exists
   * @return - true = private  key exists.
   */ 
  boolean checkPrivateKey(final CertificateStatus cert);


  /** Get an array of certificates associated with a given entity.
   *  @param principal
   */
  List findCert(Principal p);
  /** Get an array of certificates associated with a given entity.
   *  @param commonName
   */
  List findCert(String commonName);

  List findCert(String commonName, int lookupType);
  List findCert(String commonName, int lookupType, boolean validOnly);
  List findCert(X500Name dname, int lookupType, boolean validOnly);


  Hashtable findCertPairFromNS(String source, String target) 
    throws CertificateException, IOException;
  Hashtable findCertStatusPairFromNS(String source, 
                                     String target,
                                     boolean sourceNeedsPrivateKey) 
    throws CertificateException, IOException;
  List findDNFromNS(String name) throws IOException;


  List getValidCertificates(X500Name x500Name);
  List getValidPrivateKeys(X500Name x500Name);

  void publishCertificate(CertificateEntry certEntry);
  void updateNS(X500Name x500name);
  void updateNS(String commonName);
  void updateNS(CertificateEntry certEntry)throws Exception;
  // String getCommonName(String alias);

  void removeEntry(String commonName);
  //void addSSLCertificateToCache(X509Certificate cert);
  //void removeEntryFromCache(String commonName);
  void setKeyEntry(PrivateKey key, X509Certificate cert);
  void checkOrMakeCert(String name);
  void checkOrMakeCert(X500Name dname, boolean isCACert);
  void checkOrMakeCert(X500Name dname, boolean isCACert, TrustedCaPolicy trustedCaPolicy);
  boolean checkExpiry(String commonName);

  X509Certificate[] findCertChain(X509Certificate c);
  X509Certificate[] buildCertificateChain(X509Certificate certificate);
  X509Certificate[] checkCertificateTrust(X509Certificate certificate)throws CertificateChainException,
    CertificateExpiredException, CertificateNotYetValidException, CertificateRevokedException ;
  X509Certificate[] checkCertificateTrust(X509Certificate certificate[])
    throws CertificateChainException, CertificateExpiredException, 
    CertificateNotYetValidException, CertificateRevokedException;

  boolean checkCertificate(CertificateStatus cs,
			   boolean buildChain, boolean changeStatus);


  String getAlias(X509Certificate clientX509);
  String findAlias(String commonName);

  byte[] protectPrivateKey(List privKey,
			   List cert,
			   PrivateKey signerPrivKey,
			   X509Certificate signerCert,
			   X509Certificate rcvrCert);

  /** Extract information from a PKCS#12 PFX
   * @param pfxBytes       The DER encoded PFX
   * @param rcvrPrivKey    The private key of the receiver
   * @param rcvrCert       The certificate of the receiver
   */
  PrivateKeyCert[] getPfx(byte[] pfxBytes,
			  List rcvrPrivKey,
			  List rcvrCert);

  //X509CRL getCRL(String  distingushname);

  List getX500NameFromNameMapping(String name);

  void addToIgnoredList(String cname) throws Exception;

  X509KeyManager getClientSSLKeyManager()
    throws IllegalStateException;

  boolean isManagerReady();
  
  void installCertificate(String alias,
      X509Certificate[] certificateChain)
  throws CertificateException, KeyStoreException, NoSuchAlgorithmException, 
  UnrecoverableKeyException;
  
  X509Certificate[] establishCertChain(X509Certificate certificate,
      X509Certificate certificateReply)
  throws CertificateException, KeyStoreException;
  
  String getNextAlias(String commonName);
}
