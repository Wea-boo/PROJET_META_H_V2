# Guide de configuration du projet pour Eclipse et VSCode

## Prérequis

- SDK JavaFX (version 21.0.2 ou ultérieure)
- IDE Eclipse pour les développeurs Java ou Visual Studio Code

Téléchargez le SDK JavaFX [ici](https://gluonhq.com/products/javafx/).

## Configuration dans Eclipse

1. Clonez/téléchargez le répertoire du projet sur votre machine locale.
2. Ouvrez Eclipse et importez le projet :
   - Allez dans `Fichier > Importer`.
   - Choisissez `Projets existants dans l'espace de travail`.
   - Sélectionnez le répertoire du projet et terminez l'importation.
3. Configurez JavaFX dans Eclipse :
   - Cliquez avec le bouton droit sur le projet et sélectionnez `Chemin de construction > Configurer le chemin de construction`.
   - Ajoutez les bibliothèques SDK JavaFX à l'onglet Bibliothèques.
   - Vous pouvez retrouver ces bibliothèques dans le repertoire `libs` du projet, mais il est preferable de télécharger le SDK

## Configuration dans VSCode

1. Ouvrez le dossier contenant le projet avec VSCode.
2. Assurez-vous que l'extension Java est installée.
3. Modifiez le fichier `launch.json` avec les bons chemins de votre SDK JavaFX.

# Exécution de l'application

## Dans Eclipse:

1. Configurez une nouvelle Configuration de lancement pour le projet avec les arguments VM suivants (ajustez le chemin du module en fonction de l'emplacement des librairies JavaFX sur votre système) :
```json
--module-path "chemin/vers/javafx-sdk-21.0.2/lib" --add-modules javafx.controls,javafx.fxml
```
2. Choisissez comme classe principale `mkp.MKPInterface`

## Dans VSCode:

1. Assurez-vous que les arguments vmArgs dans launch.json sont correctement configurés, y compris le chemin vers le SDK JavaFX.
2. Exécutez le projet en appuyant sur F5 (ou fn+F5)

# Dépannage

1. Assurez-vous d'utiliser la version appropriée de JavaFX pour votre système.
2. Vérifiez que les arguments VM sont correctement définis dans la Configuration de lancement.
3. Pour visual studio code, en cas d'erreurs continue, et si la classe mkp.MKPInterface n'est pas retrouvée meme si les configuration sont correctes, tapez `CTRL+SHIFT+P` pour ouvrir la palette des commandes, et selectionnez `Java: Clean Java Language Server Workspace`

# Autheurs

Ce projet a été réalisé par les étudiants:
### DJILLALI Mohamed Ramy (202031046618)
### ARBADJI Yasmine (202031066576)
