# SYM_LABO_02 Jee Mathieu, Kopp Olivier, Silvestri Romain

## Traitement des erreurs

Dans le cas où le serveur n'est pas disponible ou qu'il met trop de temps à répondre, une exception sera soulevée et l'application ne recevra pas de réponse. Dans notre implémentation, nous avons cependant décidé de renvoyer un message d'erreur pour en informer l'utilisateur.

Pour encore mieux informer l'utilisateur, l'idéal serait de détecter précisément l'erreur (serveur down ? page non autorisée ? ...) et de renvoyer un message précis à l'utilisateur. En effet, si on fait une requête sur une page inexistante, l'erreur n'est pas gérée.

Pour améliorer ça, nous pourrions utiliser la classe httpResponse et regarder le code de statuts de la réponse comme suis :
remplacer ce code : 

```java
Response response = client.newCall(request).execute();
            if(response != null) {
                l.handleServerResponse(response.body().string());
            }
```

par : 

```java
HttpResponse httpResponse = client.newCall(request).execute(); 
switch(httpResponse.getStatusLine().getStatusCode()){
	case 200:
		l.handleServerResponse(response.body().string());
		break;
	case 404:
		...
		break;
		.
		.
		.
}
```

## Authentification

Si une authentification est requise, cela empêche pas de faire des requêtes asynchrone, il faut cependant veiller a ce que les requêtes soit faites dans le bon ordre (authentification puis envoi de requêtes et non l'inverse).

De même, il est possible d'envoyer des requêtes différées dans certain cas. Si l'authentification nous génère une session, alors nous pourrons différer les autre requêtes jusqu'à l'expiration de la session, par contre, une fois celle ci expirée, toute les requêtes différées non envoyées seront perdues.

## Threads concurrents

Le principal problème que l'on peut rencontrer est que l'on ne contrôle pas l'ordre dans lequel les requêtes vont être traitées, lorsque l'on envoie deux requêtes, on peut très bien recevoir la réponse de la deuxième en premier, ce qui peut être perturbant pour un utilisateur.

## Ecriture différée

La transmission différée permettra d'alléger l'utilisation du réseau mais ne garantit que les requêtes soient envoyée dans le bon ordre, ce qui peut perturber l'utilisateur, voir générer des erreurs sur le serveur.



Dans le cas du multiplexage, la consommation de la bande passante sera très grande, mais cela sera plus rapide dans le cas de petite données. Si certaines requêtes sont très volumineuse, cela peut conduire a des timeout des requêtes. Si il y a une erreur, on sera également obligé de tout renvoyer.

## Transmission d'objets

a. Le principal inconvénient est que l'on ne peut pas déterminer si l'objet envoyé est correctement configuré, cela peut produire des comportement non voulu du coté du serveur. 

L'avantage est que sans validation, toutes les requêtes sont plus rapide et donc les performances de l'application s'en trouve améliorée.

b. Ce mécanisme peut être utilisé avec http, il faut cependant s'assurer que le serveur traite correctement les objets qu'il reçoit, qui doivent être parsé correctement.
Les avantages par rapport à du json ou xml est tout d'abord la retro compatibilité, qui est parfaitement gérée sans être obligé d'écrire une série de test sur la version du document. Ce mécanisme inclut également une validation, et peut être facilement interprété par de nombreux langage notamment dans le cas ou le client et le serveur ne tourne pas sur le même langage. Les limitations de ce genre de protocole, sont qu'il consomme plus de bande passante, il est inutilisable dans certain cas (par exemple si le serveur est codé en javascript)

c. 