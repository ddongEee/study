# Study : hellots

## 개발환경구성
```bash
# nvm 으로 node.js 버전별 설치 확인 
brew install nodejs-lts
node -v

npm i -g typscript
tsc -v

# *.ts 파일을 js 로 컴파일후, 해당 파일로 node 실행
npm i -g ts-node
ts-node -v
```

## 프로젝트 구성
- 프로젝트 생성자
```bash
# default 로 package.json 파일 생성
npm init --y

# npm i --save (or -S) >  runtime 의존성 관련, dependencies 기록
# npm i --save-dev (or -D) > buildTime 의존성 관련, devDependencies 등록
# 협업위해
npm i --save-dev typescript ts-node 
npm i --save-dev @types/node
# tsconfig 절대경로 설정위해 
npm i --save-dev tsconfig-paths

# typescript project 를 위해 (ts 컴파일 설정 파일) tsconfig.json 파일생성
#
tsc --init

# package.json 의 script 에 dev, build 추가 >> intellij 에서 "show npm" 으로 검색
npm run dev
npm run build
``` 
- 프로젝트 이용자
```bash
npm i
```

## 이해
- 모듈 : ts 에 export import 키워드 사용
  - 

## 기타공부
- js library 를 typescript 환경에서 쓰기위해 "@types/*" 와 같은 추가 라이브러리 필요 
  - 이는 index.d.ts 를 포함
  - Promise 와 같은 node.js 혹은 웹브라우저가 제공하는 타입도 바로 사용안됨.(@types/node 추가해줘야함)
- index.ts 는 엔트리함수
- 개발시 ts-node 사용하지만, 프로덕션 사용시에는 ES5 변환후 node 로 실행
- tsconfig-paths 관련 : https://medium.com/@jsh901220/typescript-node-absolute-path-5782b584e368

- 아래 설정 적용해 보기
```json
{
  "script": {
    "start-dev": "NODE_ENV=development ts-node-dev --transpile-only --respawn -r tsconfig-paths/register --watch ./src/**/*.pug,./src/**/*.graphql --trace-warnings --trace-uncaught -- ./src/index.js",
    "start": "NODE_ENV=production ts-node --transpile-only -r tsconfig-paths/register src/index.js"
  }
}
```
## References
