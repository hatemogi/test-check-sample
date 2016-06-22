(ns test-check-sample.crypto-test
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [test-check-sample.crypto :refer :all]))

(def base64-상호변환
  (prop/for-all [v gen/bytes]
                (= (seq v)
                   (seq (decode-base64 (encode-base64 v))))))

(defspec base64-test
  100
  base64-상호변환)

(def 서명검증
  (prop/for-all [data gen/bytes
                 keypair (gen/fmap generate-keypair
                                   (gen/elements [512 512 512 1024 1024 2048]))]
                (verify (:public keypair) data
                        (sign (:private keypair) data))))

(defspec signature-test
  10
  서명검증)
