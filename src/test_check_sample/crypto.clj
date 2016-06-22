(ns test-check-sample.crypto
  (:import [java.security KeyFactory KeyPairGenerator SecureRandom Signature]
           [java.security.spec PKCS8EncodedKeySpec X509EncodedKeySpec]
           [java.util Base64]
           [javax.crypto SecretKeyFactory]
           [javax.crypto.spec PBEKeySpec]))

(defn encode-base64 [bytes]
  "일반 Base64 인코딩"
  (.encodeToString (Base64/getEncoder) bytes))

(defn decode-base64 [bytes]
  "일반 Base64 디코딩"
  (.decode (Base64/getDecoder) bytes))

(defn pbkdf2 [password salt iterations derived-bits]
  "PBKDF2 해쉬값을 base64로 인코딩한 문자열로 생성"
  (let [spec (PBEKeySpec. (.toCharArray password) (.getBytes salt) iterations derived-bits)
        factory (SecretKeyFactory/getInstance "PBKDF2WithHmacSHA1")]
    (->> (.generateSecret factory spec)
         (.getEncoded)
         (encode-base64))))

(defn generate-keypair
  ([] (generate-keypair 2048))
  ([keysize-bits]
   (-> (doto (KeyPairGenerator/getInstance "RSA")
         (.initialize keysize-bits))
       (.genKeyPair)
       bean
       (select-keys [:private :public]))))

(defn sign [private bytes]
  ;; https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#Signature
  (-> (doto (Signature/getInstance "SHA256withRSA")
        (.initSign private)
        (.update bytes))
      (.sign)))

(defn verify [public bytes signature]
  (-> (doto (Signature/getInstance "SHA256withRSA")
        (.initVerify public)
        (.update bytes))
      (.verify signature)))
