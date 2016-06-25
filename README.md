# 클로저의 test.check로 하는 강력한 테스팅

이 프로젝트는, 클로저를 기본으로 속성 기반 테스팅을 소개하고 간단한 데모를 위한 목적으로 만들었습니다. 제가 공부하려는 목적으로 정리했으나, 다른 프로그래밍 언어에서도 유용하게 쓸 수 있는 테스팅 방법이므로, 클로저 개발자가 아니더라도 한 번 읽어봐 주시고, 도움이 된다면 좋겠습니다.

### 한 줄 요약

> 속성 기반 테스팅을 한 번 배워 봅시다. 그러면 더욱 탄탄한 코드를 작성하는 훌륭한 프로그래머가 된....다고 합니다.

## 속성 기반 테스팅이 뭔가요?

`clojure.test`와 같은 유닛 테스트는, 입력값과 그에 따른 기댓값을 손수 나열해서, 작성한 함수가 정상 작동하는지를 확인합니다. 특정 기능을 잘 테스트하기 위해 잘 작동할 안전한 입력값도 넣고, 또 오류가 발생할 것 같은 경계에 있어 보이는 위험한 입력값을 적기도 합니다. 그렇게 지금 작성하는 함수가 정상 작동하는지도 확인하고, 나중에라도 코드를 재작성하다가 문제가 드러나지는 않는지 확인하기 좋습니다.

예를 들어, 배열을 정렬하는 함수(sort)를 작성했다고 가정해 보면요, 대략 다음과 같은 유닛 테스트를 작성할 수 있습니다.

 * 빈 배열을 정렬한 결과는 빈 배열이어야 한다.
 * `[1]`을 정렬한 결과는 `[1]`이어야 한다.
 * `[2 1 3 0]`를 정렬하면 `[0 1 2 3]`이 된다.

이렇게 말이죠.

그런데, 조금 다른 각도에서 접근해서 더 탄탄하게 테스트하는 방식이 있습니다. 위에서처럼 수동으로 몇몇 테스트 값을 수동으로 기재하고 확인하는 방식이 아니라, 작성한 **함수의 속성**만을 지정하고, 그 실제 입력값은 테스팅 도구가 임의로 자동 생성해서 확인합니다. 이것을 **속성 기반 테스팅(property-based testing)**이라고 부릅니다.

조금 전 예로 든 함수를 속성 기반으로 다시 표현한다면 아래처럼 적습니다.

> 임의의 정수 배열을 정렬해서 임의의 두 아이템을 놓고 보면, 첫 번째 아이템이 두 번째 아이템보다 작거나 같아야 한다.

이렇게 선언해 두면, 테스팅 툴이 무작위로 임의의 배열을 마구 생성해서 호출해 보고, 임의의 두 요소를 뽑아서 확인하다가, 실패하는 경우가 발견되면, 그 무작위 샘플 값과 함께 리포팅해줍니다. 오! 게다가 심지어, 실패하는 최소한의 값으로 축약해서 보여주는 아름다운 기능도 있습니다.

원래는 하스켈(Haskell)언어의 QuickCheck라는 도구가 나오고, 다른 언어로도 많이 퍼진 듯합니다. 마치 스몰토크의 SUnit으로 시작된 테스트 주도 프로그래밍이 전 세계 모든 프로그래밍 언어로 전파된 것과 비슷해 상황인 거죠. 이하 더 자세하게 알아볼게요.

## 왜 속성 기반 테스팅을 해야 하나요?

저는 클로저로 개발하고 있지만, 계속 읽으실 만한 유혹을 남겨두겠습니다. 아마도 여러분이 쓰시는 언어로도 분명 속성 기반 테스팅 툴이 있을 것입니다. [위키피디아 QuickCheck 항목](https://en.wikipedia.org/wiki/QuickCheck)을 보면, Go, Java, JavaScript, Ruby, Swift 등 각종 언어의 속성 기반 테스팅 툴 링크가 걸려 있습니다.

좋습니다. 일단 여러분이 쓰시는 언어에서 쓸 수 있기는 한데, 왜 써야 하나요? 그건, 훨씬 강력한 테스팅이 가능하기 때문인데요, 보통의 유닛 테스트로 잡기 힘든 버그까지도 쉽게(?) 찾아낼 수 있다는 장점 때문입니다.

아래에 소개드릴 동영상에 언급된, [클로저 1.5까지 있던 찾기 어려운 버그](http://dev.clojure.org/jira/browse/CLJ-1285)도, 속성 기반으로 검증했다면, 문제를 발견하기 쉬웠을 것입니다. 이 버그는 Zach Tellman이 발견했고, [Clojure Dev 메일링 리스트](https://groups.google.com/forum/#!msg/clojure-dev/HvppNjEH5Qc/1wZ-6qE7nWgJ)에서 토론이 이뤄졌는데요, 딱 봐도 발견하기도 어렵고 재현하기도 힘든 버그로 보입니다.

속성기반으로 테스팅을 했었다면 보다 빨리 발견하고, 해결할 수 있었을지도 모릅니다. 마찬가지로, 우리의 코드에 숨어있는 오류들도 속성 기반 테스팅과 함께라면 더 빨리 문제를 찾아낼 수 있을 것 같습니다.

오! 써봐야겠습니다. 이하, 클로저 언어 기준으로 설명 이어지니 참고하세요.

## 클로저용 속성 기반 테스팅: test.check

아래 깃헙 프로젝트에서 자세한 설명을 볼 수 있고,

* [GitHub — clojure/test.check: QuickCheck for Clojure](https://github.com/clojure/test.check)

아래 유투브 영상에서 자세한 설명을 들을 수 있습니다.

* [Reid Draper의 clojure.test.check 발표 영상](https://youtu.be/JMhNINPo__g)

## 사용법

### 프로젝트 의존성 추가

```clojure
[org.clojure/test.check "0.9.0"]
```

## 연습 프로젝트 받아서 돌려보기

우선, 아래 프로젝트를 받아서 한번 돌려보시고 계속 보시면 좋을 것 같습니다.

``` bash
$ git clone https://github.com/hatemogi/test-check-sample
$ cd test-check-sample
$ lein test
```

돌려보시면, 상황에 따라 한 건이나 두 건의 테스트가 실패합니다.우선 넘어가고 아래에 자세한 설명 드리겠습니다.

## 예제 설명

### 기존 유닛 테스트 케이스

우선, 클로저에서의 보통 유닛 테스트 케이스를 볼까요?

전체소스: [test/test_check_sample/unit_test.clj>](https://github.com/hatemogi/test-check-sample/blob/master/test/test_check_sample/unit_test.clj)

```clojure
(ns test-check-sample.unit-test
  (:require [clojure.test :refer :all]))

(deftest unit-test
  (testing "일반 유닛 테스트 예제"
    (is (= 4 (+ 2 2)))
    (is (instance? Long 256))
    (is (.startsWith "가나다라마" "가나"))))
```

`clojure.test`의 함수와 매크로로 테스트케이스를 정의했습니다. 보시다시피 구체적인 입력값과 기대하는 결과값을 정의하는 방식입니다.

### 첫번째 속성 기반 테스트

전체소스: [test/test_check_sample/basic_test.clj>](https://github.com/hatemogi/test-check-sample/blob/master/test/test_check_sample/basic_test.clj)

우선 필요한 다른 네임스페이스를 적절히 참조합니다.

```clojure
(ns test-check-sample.basic-test
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]))
```

이거저거 참조할 게 좀 많네요.

그다음, 처음에 예로 든, sort 함수의 속성을 실제 코드로 작성하면 아래와 같습니다.

```clojure
(defspec 정렬결과-테스트 100
  (prop/for-all [v (gen/not-empty (gen/vector gen/int))]
                (let [s (sort v)]
                  (< (first s) (last s)))))
```

## 관련 문서

 * [API 문서](http://clojure.github.io/test.check/)
 * [Cheatsheet](https://github.com/clojure/test.check/blob/master/doc/cheatsheet.md)

## 라이선스

Copyright © 2016 Daehyun Kim

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
