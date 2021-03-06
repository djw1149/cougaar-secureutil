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
import org.cougaar.core.mts.MessageAddress;
import org.cougaar.core.security.crypto.SecureMethodParam;
import org.cougaar.core.security.crypto.ProtectedObject;
import org.cougaar.core.security.policy.CryptoPolicy;

import java.io.IOException;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignedObject;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;

/** Service for most common public key cryptographic operations.
 *  Use: cryptographic aspect.
 */
public interface EncryptionService extends Service {


  /**
   * Returns the Principal that signed the data over the recent socket 
   * connection.  Used when receiving a message and the underlying mechanism
   * is RMI/SSL.
   */
  public String getRemotePrincipal();


  /**
   * Returns whether or not the send needs a signature when encryption
   * is available.
   */
  public boolean sendNeedsSignature(String source, String target)
    throws GeneralSecurityException;

  /**
   * Returns whether or not a received message needs a signature
   * when the socket is encrypted.
   */
  public boolean receiveNeedsSignature(String source)
    throws GeneralSecurityException;

  /**
   * Sets that sending a message from the source needs a signature when
   * using SSL.
   */
  public void setSendNeedsSignature(String source, 
                                    String target);

  /**
   * Sets that sending a message from the source does not need a signature
   * when using SSL.
   */
  public void removeSendNeedsSignature(String source,
                                       String target, 
                                       X509Certificate targetCert);

  /**
   * Checks that a received message has a valid signature when using SSL.
   */
  public boolean getReceiveSignatureValid(String source);

  /**
   * Sets that a received message has a valid signature when using
   * SSL.
   */
  public void setReceiveSignatureValid(String source, X509Certificate cert);

  /**
   * Encrypt a secret key with a certificate's public key
   */
  public byte[] encryptSecretKey(String algorithm, SecretKey skey,
                                 X509Certificate cert) 
    throws GeneralSecurityException;

  /**
   * Decrypt a secret key with the secret key for the given certificate
   */
  public SecretKey decryptSecretKey(String publicKeyAlg, byte[] sKeyBytes,
                                    String secretKeyAlg,
                                    X509Certificate cert)
    throws GeneralSecurityException;

  /** Sign an object.
   *
   *  @param signerName  the common name of the signer. Should be the
   *  name of an agent.
   *  @param signAlgSpec the signature algorithm specification.
   *  @param object      the object to be signed.
   *  @return a signed object.
   */
  public SignedObject sign(String signerName,
			   String signAlgSpec,
			   Serializable object)
    throws GeneralSecurityException, IOException;

  /** Verify the signature of a signed object.
   *
   *  @param signerName   the common name of the signer. Should be the
   *  name of an agent.
   *  @param signAlgSpec  the signature algorithm specification.
   *  @param signedObject the signed object.
   *  @return the object if the signature is valid.
   */
  public Object verify(String signerName,
		       String signAlgSpec,
		       SignedObject signedObject)
    throws CertificateException;

  public Object verify(String signerName,
		       String signAlgSpec,
		       SignedObject signedObject,
                       boolean expiredOk)
    throws CertificateException;

  /** Encrypt an object using public-key encryption.
   *
   *  @param targetName    the common name of the agent to which this
   *  object is sent. Note that an agent may encrypt an object for
   *  later retrieval by itself, in which case the targetName is the
   *  agent itself.
   *  @param cipherAlgSpec the cipher algorithm specification used
   *  for this operation.
   *  @param object        the object to be encrypted.
   *  @return the encrypted object
   */
  public SealedObject asymmEncrypt(String targetName,
				   String cipherAlgSpec,
				   SecretKey skey,
				   java.security.cert.Certificate cert)
    throws GeneralSecurityException, IOException;

  /** Decrypt an encrypted object using public-key encryption.
   *
   *  @param targetName    the common name of the agent to which this
   *  object is sent. Note that an agent may encrypt an object for
   *  later retrieval by itself, in which case the targetName is the
   *  agent itself.
   *  @param cipherAlgSpec the cipher algorithm specification used
   *  for this operation.
   *  @param sealedObject  the encrypted object.
   *  @return the decrypted object
   */
  public SecretKey asymmDecrypt(String targetName,
			     String cipherAlgSpec,
			     SealedObject sealedObject)
    throws CertificateException;

  /** Encrypt a message using secret key encryption.
   *
   *  @param secretKey     the secret key used to encrypt the object.
   *  @param cipherAlgSpec the cipher algorithm specification used
   *  for this operation.
   *  @param object        the object to be encrypted.
   *  @return the encrypted object
   */
  public SealedObject symmEncrypt(SecretKey secretKey,
				  String cipherAlgSpec,
				  Serializable object)
    throws GeneralSecurityException, IOException;

  /** Decrypt an encrypted object using secret key encryption.
   *
   *  @param secretKey     the secret key used to encrypt the object.
   *  @param sealedObject  the encrypted object.
   *  @param symmSpec      the key spec for the decryption key
   *  @return the decrypted object
   *  @see createSecretKey
   */
  public Object symmDecrypt(SecretKey secretKey,
			    SealedObject sealedObject,
                            String symmSpec);


  public ProtectedObject protectObject(Serializable object,
				       MessageAddress sourceAgent,
				       MessageAddress targetAgent,
				       SecureMethodParam policy)
    throws GeneralSecurityException, IOException;

  public Object unprotectObject(MessageAddress source,
				MessageAddress target,
				ProtectedObject envelope,
				SecureMethodParam policy)
    throws GeneralSecurityException;

  public ProtectedObject protectObject(Serializable object,
				       MessageAddress sourceAgent,
				       MessageAddress targetAgent,
				       CryptoPolicy policy)
    throws GeneralSecurityException, IOException;

  public Object unprotectObject(MessageAddress source,
				MessageAddress target,
				ProtectedObject envelope,
				CryptoPolicy policy)
    throws GeneralSecurityException, IOException;

  public Cipher getCipher(String spec)
    throws NoSuchAlgorithmException, NoSuchPaddingException, 
    NoSuchProviderException;

  public void returnCipher(String spec, Cipher ci);

  /**
   * Creates a secret key from a symmetric key spec.
   * The spec format is similar to the algorithm
   * used in the getInstance with two additions. The
   * transformation (RC4/DES/etc) can be followed by
   * a hash(#) and key length. The provider can follow
   * the entire algorithm in curly braces. A complex
   * example is:<p>
   * <tt>AES#192/CBC/WithCTS{BC}</tt>
   * <p>
   * The key length and provider are optional.
   *
   * @param spec The symmetric spec described above
   * @return A new secret key following the given spec
   */
  public SecretKey createSecretKey(String spec) 
    throws NoSuchAlgorithmException, NoSuchProviderException;

}

