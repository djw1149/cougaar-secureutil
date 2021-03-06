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

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;

import org.cougaar.core.component.Service;

/** Low-level service to update and retrive certificates and private keys from the Certificate Cache 
 */
public interface CRLCacheService extends Service {
  //boolean isCertificateInCRL(X509Certificate subjectCertificate, String IssuerDN);
  void addToCRLCache(String dnname);
  long getSleeptime();
  void setSleeptime(long sleeptime);
  public X509CRL createCRL(X509Certificate caCert, X509CRL caCRL,
      X509Certificate clientCert,
      X509Certificate clientIssuerCert,
      PrivateKey caPrivateKey,
      String crlSignAlg ) 
  throws NoSuchAlgorithmException, InvalidKeyException,
	CertificateException, CRLException, NoSuchProviderException,
	SignatureException,IOException;
  public X509CRL createEmptyCrl(String caDN, PrivateKey privatekey,String algorithm) 
  throws 
  	CRLException,NoSuchAlgorithmException, 
		InvalidKeyException, NoSuchProviderException, SignatureException ,IOException;
  
}
