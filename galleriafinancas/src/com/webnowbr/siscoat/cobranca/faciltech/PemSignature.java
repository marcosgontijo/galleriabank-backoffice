package com.webnowbr.siscoat.cobranca.faciltech;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jcajce.provider.asymmetric.x509.CertificateFactory;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

public class PemSignature {
	private final X509Certificate publicCertificate;
	private final PrivateKey privateKey;

	public PemSignature(
			String pemContentOfCertificate,
			String pemContentOfPrivateKey) throws IOException, CertificateException {
		PemObject pemObjectOfCertificate = GetPemObject(pemContentOfCertificate);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(pemObjectOfCertificate.getContent());
			
		CertificateFactory certificateFactory = new CertificateFactory();
		this.publicCertificate = (X509Certificate)certificateFactory.engineGenerateCertificate(inputStream);

		PemObject pemObjectOfPrivateKey = GetPemObject(pemContentOfPrivateKey);
		PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(pemObjectOfPrivateKey.getContent());
		
		JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
		this.privateKey = converter.getPrivateKey(privateKeyInfo);
	}
	
	public byte[] Sign(byte[] data) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
		String signatureAlgorithm = publicCertificate.getSigAlgName();
		Signature signer = Signature.getInstance(signatureAlgorithm);
		
		signer.initSign(this.privateKey);
	    signer.update(data);
	    byte[] result = signer.sign();
	
		return result;
	}
	
	private static PemObject GetPemObject(
            String pemContent) throws IOException {
		PemReader pemReader = new PemReader(new StringReader(pemContent));
		PemObject result = pemReader.readPemObject();
		pemReader.close();
		return result;
	}
}