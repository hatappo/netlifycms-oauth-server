# netlifycms-oauth-server

This is GitHub OAuth2 server for [NetlifyCMS](https://www.netlifycms.org/) , which is intended to deployed to [Firebase Functions](https://firebase.google.com/docs/functions).


## Set up development environment

Fork and clone this repo. Then,

```sh
npm install
```


## Set up GitHub OAuth app

Vist https://github.com/settings/developers (or `https://github.com/organizations/<YOUR_GITHUB_ORG>/settings/applications`) and register a new OAuth application.

- `Application name` -  free
- `Homepage URL` -  free
- `Application description` - free
- `Authorization callback URL` - `https://asia-northeast1-<YOUR_FIREBASE_PROJECT_ID>.cloudfunctions.net/oauth`

If you don't have Firebase projects yet here, you can change the callback url after created it.


## Configure Netlify CMS

In yours projects modify `config.yml` file:

```yaml
backend:
  name: github
  repo: <YOUR_GITHUB_ID_OR_ORG>/netlifycms-oauth-server
  branch: main  # Or branch you want to update
  base_url: https://asia-northeast1-<YOUR_FIREBASE_PROJECT_ID>.cloudfunctions.net
  auth_endpoint: /oauth/auth
```


## Set up Firebase Functions

```
npx firebase login
npx firebase use <YOUR_FIREBASE_PROJECT_ID>
npx firebase functions:config:set oauth.client_id=<GITHUB_OAUTH_APP_CLIENTID> oauth.client_secret=<GITHUB_OAUTH_APA_CLIENTSECRET>
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

npx firebase login
npx firebase use <YOUR_FIREBASE_PROJECT_ID>
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
npx firebase use <YOUR_FIREBASE_PROJECT_ID>
npx firebase deploy --only functions:oauth
```


### release simple example one for debug

```
npx shadow-cljs release example-functions
npx firebase deploy --only functions:example
```

## TODOS

- [ ] CI/CD. But this type of app is not frequently released.
- [ ] Other Git Service support e.g. GitLab, GitHub Enterprise
