(ns test-check-sample.basic-test
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]))

(defspec 정렬결과-테스트 100
  (prop/for-all [v (gen/vector gen/int)]
                (every? (fn [[a b]] (<= a b))
                        (partition 2 1 (sort v)))))

(defspec 정렬-멱등성-테스트 100
  ;; 멱등성: 연산을 여러 번 적용하더라도 결과가 달라지지 않는 성질
  (prop/for-all [v (gen/vector gen/int)]
                (= (sort v) (sort (sort v)))))

(defspec 역순-테스트
  (prop/for-all [xs (gen/vector gen/int)
                 ys (gen/vector gen/int)]
                (= (reverse (concat xs ys)) (concat (reverse ys) (reverse xs)))))

(defspec 실패범위-추려내기 100
  (prop/for-all [v (gen/vector gen/int)]
                (not (some #{42} v))))
