(ns test-check-sample.core-test
  (:require [clojure.test :refer :all]
            [test-check-sample.core :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]))

(def sort-idempotent-prop
  (prop/for-all [v (gen/vector gen/int)]
                (= (sort v) (sort (sort v)))))

(defspec core-test
  100
  sort-idempotent-prop)
