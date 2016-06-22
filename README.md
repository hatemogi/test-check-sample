# 클로저 test.check 연습

## 속성 기반 테스팅이 뭔가요?

클로저의 test.check로 하는 강력한 테스팅 clojure.test와 같은 유닛 테스트는, 입력값과 그에 따른 기대값을 나열해서, 작성한 함수가 정상 작동하는지를 확인합니다. 특정 기능을 잘 테스트하기 위해 잘 작동할 안전한 입력값도 넣어보고, 또 오류가 발생할 것 같은 경계에 있는 위험한 입력값을 선택해서 작성하기도 합니다. 지금 작성하는 함수가 정상 작동하는지도 확인하고, 나중에라도 코드를 재작성하다가, 문제가 드러나지는 않는지 확인하기에 유용합니다.

그런데, 조금 다른 각도에서 접근해서 특정 기능을 탄탄하게 테스트하는 방식이 있습니다. 수동으로 몇몇 테스트 값을 기재하고 확인하는 방식이 아니라, 작성한 함수의 속성만을 지정하고, 그 실제 입력값은 테스팅 도구가 임의로 생성해 내서 확인하는 방법이고, 이것을 "속성 기반 테스팅"(property-based testing)이라고 부릅니다.

원래는 하스켈(Haskell)언어의 QuickCheck라는 도구가 나오고, 다른 언어로도 많이 퍼진듯 합니다. 마치 스몰토크의 SUnit으로 시작된 테스트 주도 프로그래밍이 전세계 모든 프로그래밍 언어로 전파된 것과 비슷해 상황인 거죠.

## 왜 속성 기반 테스팅을 해야 하나요?

저는 클로저로 하고 있지만, 계속 읽으실 만한 유혹을 남겨두겠습니다. 아마도 여러분이 쓰시는 언어로도 분명 속성 기반 테스팅 툴이 있을 것입니다. [위키피디어 QuickCheck 항목](https://en.wikipedia.org/wiki/QuickCheck)을 살펴보니,C, C++,Erlang, Go, Java, JavaScript, Ruby, Objective-C, Swift 등의 언어의 속성 기반 테스팅 툴 링크를 볼 수 있습니다.

좋습니다. 일단 여러분이 쓰시는 언어에서 쓸 수 있기는 한데, 왜 써야 하나요? 그건, 훨씬 강력한 테스팅이 가능하기 때문인데요, 보통의 유닛 테스트로 잡기 힘든 버그까지도 쉽게(?) 찾아 낼 수 있다는 장점 때문입니다.


## 클로저용 속성 기반 테스팅: test.check

[GitHub — clojure/test.check: QuickCheck for Clojure](https://github.com/clojure/test.check)

## 사용법

### Leiningen

```clojure
[org.clojure/test.check "0.9.0"]
```

## 연습 프로젝트 받아서 돌려보기

``` bash
$ git clone https://github.com/hatemogi/test-check-sample
$ cd test-check-sample
$ lein test
```

## 관련 문서

 * [API 문서](http://clojure.github.io/test.check/)
 * [Cheatsheet](https://github.com/clojure/test.check/blob/master/doc/cheatsheet.md)

## 라이선스

Copyright © 2016 Daehyun Kim

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
