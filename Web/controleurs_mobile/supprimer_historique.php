<?php
    require_once('../modeles/connexion.php');

    $coBd = new Connexion("paris-bethanie_membre_test");
    $co = $coBd->connexion();

    if (!empty($_GET['historique'])){
        $h = $_GET['historique'];
        mysqli_query($co,"UPDATE historiquepersonne SET supprime=true WHERE id_historique = $h");
        echo "Succès";
    } else {
        echo "Erreur";
    }

?>