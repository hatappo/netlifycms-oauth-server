# netlifycms-oauth-server

This is GitHub OAuth2 server for [NetlifyCMS](https://www.netlifycms.org/) , which is intended to deployed to [Firebase Functions](https://firebase.google.com/docs/functions).


## Set up development environment

```sh
# git clone ${repo_url}
# cd ${prj_name}

npm install
```


## Set up GitHub OAuth app

Vist https://github.com/settings/developers and register a new OAuth application.

- `Application name` -  free
- `Homepage URL` -  free
- `Application description` - free
- `Authorization callback URL` - `https://asia-northeast1-${firebase_prj_id}.cloudfunctions.net/oauth`


## Set up Firebase Functions

```
npx firebase use ${firebase_prj_id}
npx firebase functions:config:set oauth.client_id=${github_oauth_app_clientid} oauth.client_secret=${github_oauth_apa_clientsecret}
```


## commands

### local development

build

```
npx shadow-cljs watch app
```

run server

```
node out/netlifycms-oauth-server.js
```

connect repl

```
npx shadow-cljs cljs-repl
```


### local Firebase Functions emulator server

```sh
cp        package.json functions/
cp .runtimeconfig.json functions/
# or
# npx firebase functions:config:get > functions/.runtimeconfig.json

npx shadow-cljs compile functions

npx firebase use ${prj_id}
npx firebase serve -p 5001
```


### release to Firebase Functions

build release js file

```
npx shadow-cljs release functions
cp package.json functions/
```

deploy to Firebase Function

```
npx firebase use ${prj_id}
npx firebase deploy --only functions:netlify-oauth
```


### release simple example one for debug

```
npx shadow-cljs release example-functions
npx firebase deploy --only functions:example
```

## TODOS

- [ ] CI/CD.
- [ ] Other Git Service support e.g. GitLab, GitHub Enterprise
- [ ] Specify `timeoutSeconds` , `memory` etc.
