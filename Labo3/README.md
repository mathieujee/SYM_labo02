# SYM_LABO_03 Jee Mathieu, Kopp Olivier, Silvestri Romain

## NFC :

### A partir de l’API Android concernant les tags NFC4, pouvez-vous imaginer une autre approche pour rendre plus compliqué le clonage des tags NFC ? Est-ce possible sur toutes les plateformes (Android et iOS), existe-il des limitations ? Voyez-vous d’autres possibilités ?

Les tags NFC ne possèdent, de base, aucune protection contre le clonage. Cependant, la plupart des tags NFC sont dotés d'un identifiant unique. On pourrait alors imaginer que l'application vérifie cet identifiant avant de prendre en compte le reste des données transmises par le tag. Malheureusement, il est toujours possible pour un attaquant de récupérer et de copier l'identifiant. Le chiffrer ne serait d'aucune utilité. Il serait effectivement possible de récupérer la clé de chiffrement en faisant du reverse-engineering sur le code. La seule façon de stocker cette clé de manière sécurisée serait de le faire du côté backend. Dans ce cas, l'application ne serait plus utilisable sans connexion au serveur, ce qui n'est pas forcément très pratique. 

La solution la plus adaptée serait d'insérer une clé asymétrique privée dans le tag et de signer un challenge cryptographique avec cette même clé. Ainsi, lors de la vérification de l'authenticité du tag par l'application, il suffirait de demander la signature du tag pour un challenge aléatoire et de vérifier cette signature avec la clé publique correspondante. Cette solution est implémentée ici: Inside Secure's VaultIC.

Il est donc impossible de prévenir le clonage de tag NFC. En revanche, il est possible de détecter si un tag est valide ou a été cloné. Peu importe la plateforme utilisée, si aucune librairie implémentant la solution présentéé précédement n'existe, il est possible de l'implémenter soi même, *à la main*.

Les limites de sécurité se trouvent directement sur le tag. En effet, les tags NFC n'ont pas été créés dans un but de sécurité. C'est donc une limite matérielle. Il serait donc envisageable, en tant que possibilité supplémentaire, de repenser le tag NFC en prêtant une attention particulière à sa sécurité.



*Source: https://stackoverflow.com/questions/22878634/how-to-prevent-nfc-tag-cloning*

## Codes-barres :

### Comparer la technologie à codes-barres et la technologie NFC, du point de vue d'une utilisation dans des applications pour smartphones, dans une optique :

### • Professionnelle (Authentification, droits d’accès, stockage d’une clé)

### • Grand public (Billetterie, contrôle d’accès, e-paiement)

### • Ludique (Preuves d'achat, publicité, etc.)

### • Financier (Coûts pour le déploiement de la technologie, possibilités de recyclage, etc.)

Dans un cadre professionnel (authentification, droit d'accès, stockage d'une clé) la technologie à codes-barres, ou *QR Code*, est une très mauvaise solution. En effet, n'importe quel individu possédant un smartphone serait en mesure de récupérer ou d'avoir accès à des données confidentielles en scannant le cryptogramme. En revanche, la technologie NFC serait ici une bien meilleure approche. Dans le but d'une authentification ou de droits d'accès, de part sa contrainte de proximité, le NFC est une bonne solution. Par exemple, un employé serait obligé de se trouver devant le lecteur NFC avec son propre tag pour avoir accès au bâtiment de son entreprise. Cependant, nous avons vu, au travers des questions sur la section NFC, que le stockage de clés n'est pas envisageable avec des tags NFC.

Les deux technologies sont très appréciées par le grand public grâce notamment à leur facilité d'utilisation. Il est en effet très facile d'imprimer son billet (avec un *QR Code* dessus) pour aller voir son groupe préféré ou de présenter le billet directement depuis son smartphone lors de l'entrée. Grâce à cette technologie, le processus de contrôle des billets a également été facilité et accéléré. Avec la technologie NFC, il est possible, par exemple, de donner accès à une salle ou un bâtiment aux employés avec un contrôle d'accès automatique: plus besoin d'avoir une persone chargée de la sécurité d'une salle ou d'un bâtiment. Le NFC permet également d'améliorer le confort du grand public lors de paiements. Il existe cependant encore quelques failles de sécurité à ce niveau là. Le paiement par NFC comporte donc actuellement encore quelques risques. 

Le *QR Code* peut être utilisé de manière ludique et créer une interaction avec l'utilisateur. Par exemple: un panneau publicitaire comportant un *QR Code* pourrait renvoyer l'utilisateur vers le site web de la compagnie ou donner plus d'informations sur le produit loué. 

D'un point de vue financier, les deux technologies sont très bien positionnées. En effet, de nos jours la majorité de la population issue des pays développés possède un smartphone et la génération de *QR Code* est totalement gratuite. Concernant les tags NFC, ceux-ci ne coûte pratiquement rien et la mise en place d'un système de lecture NFC est très peu coûteuse. Du côté du recyclage, un *QR Code* ne se recycle pas. Contrairement aux tags NFC qui sont reprogrammabless, un *QR Code* ne peut pas être réécrit. Il est obligatoire d'en regénérer un.

## Balise iBeacon

### Les iBeacons sont très souvent présentés comme une alternative à NFC. Pouvez-vous commenter cette affirmation en vous basant sur 2-3 exemples de cas d’utilisations (use-cases) concrets.

Le principal avantage qu'a les iBeacon par rapport aux balises NFC est la portée de diffusion, en effet la portée d'une balise iBeacon peu atteindre une centaine de mètres.

Voici quelque exemple d'utilisation qui peuvent être intéressante a implémenter avec des balise iBeacon :

- visite dans un musée :

  on peut imaginer une application qui réagit en fonction de la distance par rapport a différentes balise placée dans un musée, lorsque l'on se rapproche d'une œuvre, l'application nous montre ces détails.

- file d'attente à un guichet :

  On pourrais implémenter une application permettant de prendre un ticket lorsque l'on se rapproche d'une file d'attente ( par exemple à la poste ). L'application attendrais que la balise l'a contact avant de proposer à l'utilisateur de prendre un ticket virtuel.

- Offre promotionnel dans un magasin :

  Lorsque l'utilisateur se trouve dans un magasin, on peut placer des balises iBeacon permettant de diffuser des pub cohérentes en fonction de l'emplacement où il se trouve. Certains magasin l'on d'ailleurs déjà fais comme par exemple en Finlande avec la chaine K-supermarket.

## Capteur :

### Une fois la manipulation effectuée, vous constaterez que les animations de la flèche ne sont pas fluides, il va y avoir un tremblement plus ou moins important même si le téléphone ne bouge pas.

### Veuillez expliquer quelle est la cause la plus probable de ce tremblement et donner une manière (sans forcément l’implémenter) d’y remédier.
Le problème du tremblement vient probablement du nombre de mises à jour par secondes que notre application effectue. En augmentant ce nombre, le mouvement de la flèche paraitera beaucoup plus régulier au prix d'une consomation de ressources plus élevée. Il faut aussi tenir compte du taux de rafraichissement des capteurs. L'idéal serait de synchroniser le taux de l'écran avec celui des capteurs ce qui augmenterait la fluidité des mouvements de la flèche.  
Un autre problème est la sensibilité des capteurs du téléphone:  
- Le magnétomètre est très sensible à l'environnement. En effet, si quelque chose autour de lui génére un champs magnétique, la précision de la boussole sera alors impactée.
- L'acceléromètre détecte du mouvement en permance même si le téléphone ne bouge pas. La moindre vibration de son support est detecté ce qui a pour effet de modifier la flèche en permanance d'où la sensation de tremblement.

Dans ces deux cas, une amèlioration des capteurs du téléphone permet de réduire le tremblement et d'améliorer la précision de la flèche.
