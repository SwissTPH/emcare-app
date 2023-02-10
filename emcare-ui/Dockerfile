#stage 1
FROM node:latest as node

WORKDIR /app
COPY . .
ARG NG_APP_KC_URL
ARG NG_APP_API_URL
ARG NG_APP_SSO_USER
ARG NG_APP_SSO_SECRET
ARG NG_APP_QUESTIONNAIRE_BUILDER_URL 'https://emcare.argusoft.com/questionnaireBuilder'
RUN npm install -g envsub
RUN envsub  --diff /app/environment.ts.tpl  /app/src/app/shared/module/structor/src/environments/environment.ts
RUN envsub  --diff /app/environment.ts.tpl  /app/src/environments/environment.ts

RUN npm install --include=dev


RUN npm run build 
#stage 2
FROM nginx:alpine
COPY --from=node /app/dist/em-care /usr/share/nginx/html
