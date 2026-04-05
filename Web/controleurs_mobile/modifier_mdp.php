<?php
    require_once('../modeles/compte.php');
    require_once('../modeles/connexion.php');

    session_start();
    $c = $_GET['compte'];

    $coBd = new Connexion("paris-bethanie_membre_test");
	$co = $coBd->connexion() or die("Erreur de connexion !");

    if (!empty($_POST['pwd']) && !empty($_POST['pwdr'])){
        if ($_POST['pwd'] != $_POST['pwdr']){
            echo "Mots de passe différents";
        } else {
            $pwd = str_replace("'","''",$_POST['pwd']);

            mysqli_query($co, "UPDATE compte SET mdp='$pwd', mdp_crypted=sha1('$pwd') WHERE id_compte=$c") or die ("Erreur de modification du mot de passe");

            mysqli_query($co, "INSERT INTO historiquepersonne (texte, date_historique, heure_historique, supprime, compte_concerne) VALUES ('Vous avez modifié votre mot de passe',curdate(), curtime(), false, $c)");

            echo "Succès";
        }
    } else {
        echo "Champ vide";
    }
?>