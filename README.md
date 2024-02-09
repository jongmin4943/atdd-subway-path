# 지하철 노선도 미션
[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

# 인수 테스트와 TDD
## 0단계 - 단위 테스트 작성
### 요구사항
- 지하철 구간 관련 단위 테스트 작성
  - [x] 구간 단위 테스트
    - [x] 구간 테스트를 위한 지하철역 테스트
    - [x] 구간 테스트를 위한 노선 테스트
  - [x] 구간 서비스 단위 테스트 with Mock
  - [x] 구간 서비스 단위 테스트 without Mock
- 단위 테스트를 기반으로 비지니스 로직 리팩터링

## 1단계 - 구간 추가 요구사항 반영
### 사용자 스토리
>사용자로서<br>
>지하철 노선도를 조금 더 편리하기 관리하기 위해<br>
>위치에 상관없이 지하철 노선에 역을 추가 할 수 있다
### 요구사항
- 노선에 역 추가시 노선 가운데에 추가 할 수 있다.
- 노선에 역 추가시 노선 처음에 추가 할 수 있다.
- 이미 등록되어있는 역은 노선에 등록될 수 없다
### 인수테스트 시나리오
#### 노선에 역 추가시 노선 가운데에 추가
- 성공 시나리오
  > Given 지하철 노선을 생성하고<br>
  > When 해당 노선 가운데 구간에 새로운 구간을 추가하면<br>
  > Then 지하철 노선 조회시 가운데에 새로운 구간이 추가된 정보가 조회된다.
- 실패 시나리오
  > Given 지하철 노선을 생성하고<br>
  > When 해당 노선 가운데 구간에 새로운 구간을 추가하는데<br>
  > When 새로운 구간의 하행역이 이미 노선에 등록되어 있다면<br>
  > Then 에러가 난다.

  > Given 지하철 노선을 생성하고<br>
  > When 해당 노선 가운데 구간에 새로운 구간을 추가하는데<br>
  > When 새로운 구간의 상행역이 해당 노선에 포함되어 있지 않다면<br>
  > Then 에러가 난다.

  > Given 지하철 노선을 생성하고<br>
  > When 해당 노선 가운데 구간에 새로운 구간을 추가하는데<br>
  > When 새로운 구간의 길이가 기존 노선의 길이보다 같거나 길다면<br>
  > Then 에러가 난다.
#### 노선에 역 추가시 노선 처음에 추가
- 성공 시나리오
  > Given 지하철 노선을 생성하고<br>
  > When 해당 노선 첫 구간에 새로운 구간을 추가하면<br>
  > Then 지하철 노선 조회시 처음에 새로운 구간이 추가된 정보가 조회된다.
- 실패 시나리오
  > Given 지하철 노선을 생성하고<br>
  > When 해당 노선 첫 구간에 새로운 구간을 추가하는데<br>
  > When 새로운 구간의 상행역이 이미 노선에 등록되어 있다면<br>
  > Then 에러가 난다.

  > Given 지하철 노선을 생성하고<br>
  > When 해당 노선 첫 구간에 새로운 구간을 추가하는데<br>
  > When 새로운 구간의 하행역이 해당 노선에 포함되어 있지 않다면<br>
  > Then 에러가 난다.
