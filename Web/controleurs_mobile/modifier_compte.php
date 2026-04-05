<?php
    require_once('../modeles/connexion.php');
    require_once('../modeles/compte.php');
    session_start();
    $coBd = new Connexion("paris-bethanie_membre_test");
    $co = $coBd->connexion() or die ("Erreur de connexion");

    if (!empty($_GET['compte'])) {
        $c = $_GET['compte'];
        $login = str_replace("'","''",$_POST['login']);
        $profil = $_POST['profil'];
        $pwd = str_replace("'","''",$_POST['pwd']);
        //$pwdr = str_replace("'","''",$_POST['pwdr']);

        $r = mysqli_query($co,"SELECT login,mdp,profil_compte FROM compte WHERE id_compte=$c");
        while ($li = mysqli_fetch_assoc($r)){
            $m = str_replace("'","''",$li['mdp']);
            $lo= str_replace("'","''",$li['login']);
            $p = $li['profil_compte'];
        }

        $result = mysqli_query($co,"SELECT count(*) as c FROM compte WHERE login='$login' and id_compte != $c");
        while ($line = mysqli_fetch_assoc($result)){
            $count = $line['c'];
        }

        if ($count != 0){
            echo "Compte existant";
        } else {
            $sql = "UPDATE compte SET login='$login', mdp='$pwd', mdp_crypted = sha1('$pwd'), profil_compte = '$profil' WHERE id_compte = $c";
            mysqli_query($co,$sql) or die ('Impossible de modifier vos informations');
            mysqli_query($co,"INSERT INTO historiquepersonne (texte,date_historique, heure_historique, supprime, compte_concerne) VALUES ('Vous avez modifié vos informations',curdate(),curtime(),false,$c)");
            echo "Succès";
        }
    } else {
        echo "Erreur";
    }
?>