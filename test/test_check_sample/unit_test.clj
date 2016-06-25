(ns test-check-sample.unit-test
  (:require [clojure.test :refer :all]))

(deftest unit-test
  (testing "일반 유닛 테스트 예제"
    (is (= 4 (+ 2 2)))
    (is (instance? Long 256))
    (is (.startsWith "가나다라마" "가나"))))
