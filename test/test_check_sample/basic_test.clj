(ns test-check-sample.basic-test
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]))

(defspec 정렬-멱등성-테스트 100
  ;; 멱등성: 연산을 여러 번 적용하더라도 결과가 달라지지 않는 성질
  (prop/for-all [v (gen/vector gen/int)]
                (= (sort v) (sort (sort v)))))

(defspec 정렬후-첫째가-마지막보다-작은지-테스트 100
  (prop/for-all [v (gen/not-empty (gen/vector gen/int))]
                (let [s (sort v)]
                  (< (first s) (last s)))))

(defspec 실패범위-추려내기 100
  (prop/for-all [v (gen/vector gen/int)]
                (not (some #{42} v))))
