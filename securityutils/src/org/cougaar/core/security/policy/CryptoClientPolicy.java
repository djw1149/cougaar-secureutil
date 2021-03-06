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


package org.cougaar.core.security.policy;

import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class CryptoClientPolicy
  extends SecurityPolicy
{
  private boolean isCertificateAuthority;

  private boolean isRootCA;

  /** The file name of the keystore containing keys for this node
   */
  private String keystoreName;

  /** The password of the keystore containing keys for this node
   */
  private String keystorePassword;

  /** The file name of the keystore containing the trusted CAs
   */
  private String trustedCaKeystoreName;

  /** The password of the keystore containing the trusted CAs.
   */
  private String trustedCaKeystorePassword;

  /** An array of trusted certificate authorities (TrustedCaPolicy).
   */
  private Vector trustedCAs;

  /** Flag to indicate whether the smart card is to be used
   * in conjunction with keystore.
   */
  private boolean useSmartCard = false;

  /**
   * For unzip & run, infoURL specifies path to CAInfoServlet
   * requestURL specifies path to certificate signing request.
   * NOTE: CA host, port, and agent is not included in the path,
   * host and agent are supplied thru plugin param and port thru prop.
   */
  private String infoURL;
  private String requestURL;

  private CertificateAttributesPolicy certificateAttributesPolicy;

  public CryptoClientPolicy() {
    trustedCAs = new Vector();
  }

  // Get methods
  public boolean isCertificateAuthority() {
    return isCertificateAuthority;
  }

  public boolean isRootCA() {
    return isRootCA;
  }

  public String getKeystoreName() {
    return keystoreName;
  }
  public String getKeystorePassword() {
    return keystorePassword;
  }
  public boolean getUseSmartCard() {
    return useSmartCard;
  }

  public String getTrustedCaKeystoreName() {
    return trustedCaKeystoreName;
  }
  public String getTrustedCaKeystorePassword() {
    return trustedCaKeystorePassword;
  }
  public CertificateAttributesPolicy getCertificateAttributesPolicy() {
    // return the cert attrib policy from the first trusted ca policy if there is one
    // the default one will be the choice if the first one uses it
    // this implies there must be a default cert attribute policy
    if (trustedCAs.size() != 0) {
      TrustedCaPolicy tc = (TrustedCaPolicy)trustedCAs.get(0);
      if (tc.getCertificateAttributesPolicy() != null) {
        return tc.getCertificateAttributesPolicy();
      }
    }
    return certificateAttributesPolicy;
  }
  public CertificateAttributesPolicy getCertificateAttributesPolicy(
    TrustedCaPolicy trustedCaPolicy) {
    if (trustedCaPolicy != null && trustedCaPolicy.getCertificateAttributesPolicy() != null)
      return trustedCaPolicy.getCertificateAttributesPolicy();

    return certificateAttributesPolicy;
  }

  public TrustedCaPolicy[] getTrustedCaPolicy() {
    TrustedCaPolicy[] tc = new TrustedCaPolicy[trustedCAs.size()];
    trustedCAs.toArray(tc);
    return tc;
  }

  public TrustedCaPolicy[] getIssuerPolicy() {
    Vector issuers = new Vector();
    for (int i = 0; i < trustedCAs.size(); i++) {
      TrustedCaPolicy trustedCaPolicy = (TrustedCaPolicy)trustedCAs.get(i);
      if (trustedCaPolicy.caURL != null) {
        issuers.addElement(trustedCaPolicy);
      }
    }
    TrustedCaPolicy[] tc = new TrustedCaPolicy[issuers.size()];
    issuers.toArray(tc);
    return tc;
  }

  // Set methods
  public void setIsCertificateAuthority(boolean isCertAuth) {
    isCertificateAuthority = isCertAuth;
  }

  public void setIsRootCA(boolean isRoot) {
    isRootCA = isRoot;
  }

  public void setKeystoreName(String keystoreName) {
    this.keystoreName = keystoreName;
  }
  public void setKeystorePassword(String keystorePassword) {
    this.keystorePassword = keystorePassword;
  }
  public void setUseSmartCard(boolean flag) {
    this.useSmartCard = flag;
  }

  public void setTrustedCaKeystoreName(String trustedCaKeystoreName) {
    this.trustedCaKeystoreName = trustedCaKeystoreName;
  }
  public void setTrustedCaKeystorePassword(String trustedCaKeystorePassword) {
    this.trustedCaKeystorePassword = trustedCaKeystorePassword;
  }

  public void setCertificateAttributesPolicy(CertificateAttributesPolicy cap) {
    this.certificateAttributesPolicy = cap;
  }
  public void addTrustedCaPolicy(TrustedCaPolicy tc) {
    trustedCAs.addElement(tc);
  }

  public String getInfoURL() {
    return infoURL;
  }
  public String getRequestURL() {
    return requestURL;
  }
  public void setInfoURL(String url) {
    infoURL = url;
  }
  public void setRequestURL(String url) {
    requestURL = url;
  }

  public String toString() {
    String s = "isCA=" + isCertificateAuthority
      + " - keystoreName=" + keystoreName
      + " - trustedCaKeystoreName=" + trustedCaKeystoreName;
    if (trustedCAs != null) {
      for (int i = 0 ; i < trustedCAs.size() ; i++) {
	s = s + "\nTrusted CA[" + i + "]:" + trustedCAs.get(i).toString();
      }
    }
    if (certificateAttributesPolicy != null) {
      s = s + "\nCertificate Attributes:" +  certificateAttributesPolicy.toString();
    }
    return s;
  }

  public Node convertToXML(Document parent) {
    Element ccPolicyNode = parent.createElement("cryptoClientPolicy");
    // is certificate authority
    Node node = parent.createElement(CryptoClientPolicyConstants.IS_CERT_AUTH_ELEMENT);    
    node.appendChild(parent.createTextNode((new Boolean(isCertificateAuthority)).toString()));
    ccPolicyNode.appendChild(node);
    
    // if this is a cert authority set the isRootCA element
    if(isCertificateAuthority) {
      node = parent.createElement(CryptoClientPolicyConstants.IS_ROOT_CA_ELEMENT);    
      node.appendChild(parent.createTextNode((new Boolean(isRootCA)).toString()));
      ccPolicyNode.appendChild(node);  
    }
    if(keystoreName != null) {
      // keystore file name
      node = parent.createElement(CryptoClientPolicyConstants.KEYSTORE_FILE_ELEMENT);    
      node.appendChild(parent.createTextNode(keystoreName));
      ccPolicyNode.appendChild(node);
    }
    if(keystorePassword != null) {
      // keystore password (BAD, we shouldn't store the keystore password in the clear!)
      node = parent.createElement(CryptoClientPolicyConstants.KEYSTORE_PASSWORD_ELEMENT);    
      node.appendChild(parent.createTextNode(keystorePassword));
      ccPolicyNode.appendChild(node);
    }
    // KEYSTORE_USE_SMART_CARD optional
    /*
    node = parent.createElement(CryptoClientPolicyHandler.KEYSTORE_USE_SMART_CARD);    
    node.appendChild(parent.createTextNode((new Boolean(useSmartCard)).toString()));
    ccPolicyNode.appendChild(node);
    */
    // begin trusted CAs
    // iterator the vector of trusted CAs
    node = parent.createElement("trustedCAs");
    Node innerNode = null;
    if(trustedCaKeystoreName != null) {
      // CA keystore
      innerNode = parent.createElement(CryptoClientPolicyConstants.CA_KEYSTORE_ELEMENT);
      innerNode.appendChild(parent.createTextNode(trustedCaKeystoreName));
      node.appendChild(innerNode);
    }
    if(trustedCaKeystorePassword != null) {
      // CA keystore password (shouldn't be storing passwords in the clear!)
      innerNode = parent.createElement(CryptoClientPolicyConstants.CA_KEYSTORE_PASSWORD_ELEMENT);
      innerNode.appendChild(parent.createTextNode(trustedCaKeystorePassword));
      node.appendChild(innerNode);
    }
    if(infoURL != null) {
      // CA info url for unzip and run
      innerNode = parent.createElement(CryptoClientPolicyConstants.CA_INFOURL_ELEMENT);
      innerNode.appendChild(parent.createTextNode(infoURL));
      node.appendChild(innerNode);
    }
    if(requestURL != null) {
      // CA request url for unzip and run
      innerNode = parent.createElement(CryptoClientPolicyConstants.CA_REQUESTURL_ELEMENT);
      innerNode.appendChild(parent.createTextNode(requestURL));
      node.appendChild(innerNode);
    }
    
    // iterator through the trusted CAs
    Iterator i = trustedCAs.iterator();
    while(i.hasNext()) {
      TrustedCaPolicy tcp = (TrustedCaPolicy)i.next();
      node.appendChild(tcp.convertToXML(parent));
    }
    ccPolicyNode.appendChild(node);
    // end trustedCAs

    if(certificateAttributesPolicy != null) {
      // certificate attributes
      ccPolicyNode.appendChild(certificateAttributesPolicy.convertToXML(parent));
    }
    
    return ccPolicyNode;
  }
};
