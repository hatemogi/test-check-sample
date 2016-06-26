# 클로저의 test.check로 하는 강력한 테스팅

이 [프로젝트][데모 프로젝트]는, 클로저를 기본으로 "속성 기반 테스팅"을 소개하고 간단한 데모를 보이기 위한 목적으로 만들었습니다. 제가 공부하려는 목적으로 정리했으나, 다른 프로그래밍 언어에서도 유용하게 쓸 수 있는 테스팅 방법이므로, 클로저 개발자가 아니더라도 한 번 읽어봐 주시고, 도움이 된다면 좋겠습니다.

### 한 줄 요약

> 속성 기반 테스팅을 한 번 배워 봅시다. 그러면 더욱 탄탄한 코드를 작성하는 훌륭한 프로그래머가 된....다고 합니다.

## 속성 기반 테스팅이 뭔가요?

`clojure.test`와 같은 유닛 테스트는, 입력값과 그에 따른 기댓값을 손수 나열해서, 작성한 함수가 정상 작동하는지를 확인합니다. 특정 기능을 잘 테스트하기 위해 잘 작동할 안전한 입력값도 넣고, 또 오류가 발생할 것 같은 경계에 있어 보이는 위험한 입력값을 적기도 합니다. 그래서 지금 작성하는 함수가 정상 작동하는지도 확인하고, 나중에라도 코드를 재작성하다가 문제가 드러나지는 않는지 확인하기 좋습니다.

예를 들어, 배열을 정렬하는 함수(sort)를 작성했다고 가정해 보면요, 대략 다음과 같은 유닛 테스트를 작성할 수 있습니다.

 * 빈 배열을 정렬한 결과는 빈 배열이어야 한다.
 * `[1]`을 정렬한 결과는 `[1]`이어야 한다.
 * `[2 1 3 0]`를 정렬하면 `[0 1 2 3]`이 된다.

이렇게 말이죠.

그런데, 조금 다른 각도에서 접근해서 더 탄탄하게 테스트하는 방식이 있습니다. 위에서처럼 수동으로 몇몇 테스트 값을 수동으로 기재하고 확인하는 방식이 아니라, 작성한 **함수의 속성**만을 지정하고, 그 실제 입력값은 테스팅 도구가 임의로 자동 생성해서 확인합니다. 이것을 **속성 기반 테스팅(property-based testing)**이라고 부릅니다.

조금 전 예로 든 함수를 속성 기반으로 다시 테스트한다면 이렇게 할 수 있습니다.

* 임의의 배열을 정렬하면, 매 인접 두 아이템은 앞의 아이템이 뒤의 아이템보다 작거나 같아야한다.
* 임의의 배열을 정렬을 한번 한 결과와, 여러번 한 결과는 같다.

이렇게 선언해 두면, 테스팅 툴이 무작위로 임의의 배열을 마구 생성해서 호출해 보고, 임의의 두 요소를 뽑아서 확인하다가, 실패하는 경우가 발견되면, 그 무작위 샘플 값과 함께 리포팅해줍니다. 오! 게다가 심지어, 실패하는 최소한의 값으로 축약해서 보여주는 아름다운 기능도 있습니다.

원래는 하스켈(Haskell)언어의 QuickCheck라는 도구가 나오고, 다른 언어로도 많이 퍼진 듯합니다. 마치 스몰토크의 SUnit으로 시작된 테스트 주도 프로그래밍이 전 세계 모든 프로그래밍 언어로 전파된 것과 비슷해 상황인 거죠. 이하 더 자세하게 알아볼게요.

## 왜 속성 기반 테스팅을 해야 하나요?

저는 클로저로 개발하고 있지만, 계속 읽으실 만한 유혹을 남겨두겠습니다. 아마도 여러분이 쓰시는 언어로도 분명 속성 기반 테스팅 툴이 있을 것입니다. [위키피디아 QuickCheck 항목](https://en.wikipedia.org/wiki/QuickCheck)을 보면, Go, Java, JavaScript, Ruby, Swift 등 각종 언어의 속성 기반 테스팅 툴 링크가 걸려 있습니다.

좋습니다. 일단 여러분이 쓰시는 언어에서 쓸 수 있기는 한데, 왜 써야 하나요? 그건, 훨씬 강력한 테스팅이 가능하기 때문인데요, 보통의 유닛 테스트로 잡기 힘든 버그까지도 쉽게(?) 찾아낼 수 있다는 장점 때문입니다.

아래에 소개드릴 동영상에 언급된, [클로저 1.5까지 있던 찾기 어려운 버그](http://dev.clojure.org/jira/browse/CLJ-1285)도, 속성 기반으로 검증했다면, 문제를 발견하기 쉬웠을 것입니다. 이 버그는 Zach Tellman이 발견했고, [Clojure Dev 메일링 리스트](https://groups.google.com/forum/#!msg/clojure-dev/HvppNjEH5Qc/1wZ-6qE7nWgJ)에서 토론이 이뤄졌는데요, 딱 봐도 발견하기도 어렵고 재현하기도 힘든 버그로 보입니다.

속성기반으로 테스팅을 했었다면 보다 빨리 발견하고, 해결할 수 있었을지도 모릅니다. 마찬가지로, 우리의 코드에 숨어있는 오류들도 속성 기반 테스팅과 함께라면 더 빨리 문제를 찾아낼 수 있을 것 같습니다.

오! 써봐야겠습니다. 이하, 클로저 언어 기준으로 설명 이어지니 참고해주세요.

## 클로저용 속성 기반 테스팅: test.check

아래 깃헙 프로젝트에서 자세한 설명을 볼 수 있고,

* [GitHub — clojure/test.check: QuickCheck for Clojure](https://github.com/clojure/test.check)

아래 test.check를 개발한 Reid Draper의 발표 영상에서 자세한 내용을 배울 수 있습니다.

* [Reid Draper의 clojure.test.check 발표 영상](https://youtu.be/JMhNINPo__g) [[슬라이드](https://speakerdeck.com/reiddraper/powerful-testing-with-test-dot-check)]

## 사용법

### 프로젝트 의존성 추가

```clojure
[org.clojure/test.check "0.9.0"]
```

여느 클로저 라이브러리 처럼 의존성을 추가하면 바로 쓸 수 있습니다.

## 연습 프로젝트 받아서 돌려보기

우선, 아래 프로젝트를 받아서 한번 돌려보시고 계속 보시면 좋을 것 같습니다.

```bash
$ git clone https://github.com/hatemogi/test-check-sample
$ cd test-check-sample
$ lein test
```

돌려보시면, 상황에 따라 한 건이 실패합니다(희박한 확률로 다 통과할 수도 있습니다). 우선 넘어가고 아래에 자세한 설명 드리겠습니다.

## 예제 설명

### 기존 유닛 테스트 케이스

우선, 클로저에서의 보통 유닛 테스트 케이스를 볼까요?

> 전체소스: [test/test_check_sample/unit_test.clj](https://github.com/hatemogi/test-check-sample/blob/master/test/test_check_sample/unit_test.clj)

```clojure
(ns test-check-sample.unit-test
  (:require [clojure.test :refer :all]))

(deftest unit-test
  (testing "일반 유닛 테스트 예제"
    (is (= 4 (+ 2 2)))
    (is (instance? Long 256))
    (is (.startsWith "가나다라마" "가나"))))
```

`clojure.test`의 함수와 매크로로 테스트 케이스를 정의했습니다. 입력값과 기대하는 결괏값을 구체적으로 정의하는 방식입니다.

### 첫 번째 속성 기반 테스트

> 전체소스: [test/test_check_sample/basic_test.clj](https://github.com/hatemogi/test-check-sample/blob/master/test/test_check_sample/basic_test.clj)

우선 필요한 다른 네임스페이스를 적절히 참조합니다.

```clojure
(ns test-check-sample.basic-test
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]))
```

참조할 네임스페이스가 좀 많네요.

그다음, 처음에 예로 든, sort 함수의 속성을 실제 코드로 작성하면 아래와 같습니다.

```clojure
(defspec 정렬결과-테스트 100
  (prop/for-all [v (gen/vector gen/int)]
                (every? (fn [[a b]] (<= a b))
                        (partition 2 1 (sort v)))))
```

처음 설명드렸던 아래 속성을 테스트하는 코드입니다.

> 임의의 배열을 정렬하면, 각각의 인접한 두 아이템은 앞의 아이템이 뒤의 아이템보다 작거나 같아야한다.


줄마다 차례로 설명드리겠습니다.

1. `defspec`은 `clojure.test`와의 호환성을 위한 매크로입니다. `clojure.test`에서 쓰는 `deftest`와 마찬가지로 테스트용 함수를 만드는 매크로라서, 기본 클로저 테스팅 환경에서 그대로 쓸 수 있게 해줍니다. 우리 프로젝트에서는 `lein test`로 평범하게 테스트할 수 있게 해주는 것이지요. 그다음 `정렬결과-테스트`라는 테스트 이름이자 함수명이 오고요, 그다음 `100`이라는 숫자는 몇 번이나 임의의 테스트 셋을 만들어 낼지를 지정합니다. 이 경우 100개의 테스트 데이터를 생성해서 진행하는 게 됩니다. 생략하면 기본값이 100이라서 이 경우 생략해도 결과는 같습니다.
1. `prop/for-all`은 매크로인데, 처음에 테스트 셋 생성하는 바인딩을 정의하고, 그다음 표현으로 실제 검증 내용이 옵니다. `gen` 네임스페이스에는 각종 생성함수가 들어있습니다. `gen/vector`는 벡터를 생성하는 함수인데, 제일 안에 있는 `gen/int`가 정수를 임의로 만들어내는 생성 함수인 겁니다. 요약하면, 임의의 정수를 담고 있는 벡터를 만들어 내는 것이지요.
1. 셋째 줄은 평범한 클로저 구문이고요, 마지막 줄에서 구한 컬렉션 전체에 대해 앞의 요소가 뒤의 요소보다 작거나 같은지를 알아봅니다.
1. 마지막 줄은 `v`를 `sort`한 결과를 `partition` 함수로 2개씩 쌍을 지어 분리해둡니다. 이 부분이 셋째 줄 every? 함수의 마지막 인수가 됩니다.

설명이 길었지만, 이해하고 나면 간단합니다.

어떤가요? 괜찮아 보이시나요?

### 속성 기반 테스트 더 보기

예제로 몇 개 더 볼게요.

```clojure
(defspec 정렬-멱등성-테스트 100
  ;; 멱등성: 연산을 여러 번 적용하더라도 결과가 달라지지 않는 성질
  (prop/for-all [v (gen/vector gen/int)]
                (= (sort v) (sort (sort v)))))
```

이 테스트는, 정렬 함수를 한번 적용하나, 두 번 적용하나 결과는 그대로 여야 한다는 속성을 검사합니다.

```clojure
(defspec 역순-테스트
  (prop/for-all [xs (gen/vector gen/int)
                 ys (gen/vector gen/int)]
                (= (reverse (concat xs ys)) (concat (reverse ys) (reverse xs)))))
```

이 함수는 xs 벡터와 ys 벡터를 합친 다음 역순으로 하는 것(reverse (concat xs ys))과, ys 벡터를 역순으로 한 것과 xs 벡터를 역순으로 하고 합친 것(concat (reverse ys) (reverse xs))이 결과는 같다는 속성을 나타냅니다.

이제 감이 오시죠? 일일이 입력값과 결괏값을 적는 것보다 정확한 테스트가 가능해 보입니다. 심지어, 일일이 테스트 셋을 기재하지 않아도 되니 편리해 보이기까지 하네요. 아... 그건 아닌가요? ㅎ

### 실패 범위 축소 기능 (shrink)

```clojure
(defspec 실패범위-추려내기 100
  (prop/for-all [v (gen/vector gen/int)]
                (not (some #{42} v))))
```

마지막으로, 결과를 축소해주는 shrink기능을 보여주는 예제입니다. 이 경우 임의 정수들을 담은 벡터들을 만들고, `(not (some #{42} v))'로 확인을 합니다. 임의 정수 벡터에 42가 없어야 한다는 속성인데요, 이는 명백히 틀린 속성이죠. 경우에 따라 정수 42가 들어있는 벡터가 있을 수도 있고 없을 수도 있습니다.

```clojure
{:result false, :seed 1466832280883, :failing-size 45, :num-tests 46,
 :fail [[-15 12 24 25 -16 -12 -45 44 39 -11 24 -22 42 -32 -41 -33 42 16 -27 -5 -6 -21 39 8 -45 -3 38 -19 -19 10 1 6 -23 -21 -35 13 -9 16 -11 -19 -36]],
 :shrunk {:total-nodes-visited 71, :depth 40, :result false, :smallest [[42]]},
 :test-var "실패범위-추려내기"}
```

테스트 리포트를 보시면, `:result false`로 실패했고, `num-test 46`번 실행하다가 실패가 발견됐습니다. 실패한 테스트 데이터는 `[-15 12 ... -36]`였습니다. 이 데이터로 테스트했을 때 왜 실패했는지를 판단하기 편리하게끔, `test.check`가 입력 데이터의 최소한의 데이터셋을 대신 자동으로 추적해 줍니다. 최소한의 데이터로 줄여 보니 `[42]`일 때 실패했다는 친절한 설명을 해줍니다. 오우~!

그러면, 이 작은 데이터를 기준으로 "왜 실패했는지"를 찾아보고 해결하면 됩니다.

100번 실행하다가 발견됐지만, 임의의 정수들을 담은 경우이므로 문제없이 통과되어 발견되지 않을 수도 있습니다. 100 대신 더 큰 수를 넣어서 여러 번 테스트하면 매번 확인할 수도 있겠습니다.

[데모 프로젝트][]에는 예제 파일이 하나 더 있으니, 참고해주세요.

> [test/test_check_sample/crypto_test.clj](https://github.com/hatemogi/test-check-sample/blob/master/test/test_check_sample/crypto_test.clj)

자세한 설명도 없이 예제를 마구 보여드렸습니다. 이제 충분한 관심이 가시고, 직접 적용해보시려면 test.check 깃헙 프로젝트에 있는 설명서를 참고해서 시작해 보시면 좋을 것 같습니다.

## 결론

이상, 더 편리하고 강력하게 테스팅해서 우리의 코드를 더 탄탄하게 작성하는 데 도움이 되는 "속성 기반 테스팅"에 대해 간단히 설명하고, 클로저 언어 환경에서 직접 한번 사용해 보았습니다.

## 참고로...

참고로, 클로저 1.9에는 test.check를 포용하는 clojure.spec이 추가됩니다!

## 관련 문서

 * [API 문서](http://clojure.github.io/test.check/)
 * [Cheatsheet](https://github.com/clojure/test.check/blob/master/doc/cheatsheet.md)

## 라이선스

Copyright © 2016 Daehyun Kim

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

[데모 프로젝트]: https://github.com/hatemogi/test-check-sample
