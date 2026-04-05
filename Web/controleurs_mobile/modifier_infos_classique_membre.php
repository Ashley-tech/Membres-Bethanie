<?php
    require_once('../modeles/connexion.php');
    require_once('../modeles/compte.php');
    session_start();
    $coBd = new Connexion("paris-bethanie_membre_test");
    $co = $coBd->connexion() or die ("Erreur de connexion");
    
    if (!empty($_GET['compte']) && !empty($_GET['personne'])) {
        $c = $_GET['compte'];
	    $p = $_GET['personne'];

        $sexe = $_POST['sexe'];
        $nom = str_replace("'","''",ucfirst(strtolower($_POST['nom'])));
        $prenom = str_replace("'","''",ucfirst(strtolower($_POST['prenom'])));
        $date = $_POST['naissance'];
        $mel = $_POST['mel'];
    
        mysqli_query($co,"UPDATE personne SET sexe='$sexe',nom='$nom',prenom='$prenom',date_naissance='$date',mel='$mel' WHERE id_personne=$p") or die("Modification d'un membre impossible");
        if ($sexe == 'F'){
            mysqli_query($co, "DELETE FROM lienpersonne where personne=$p AND lien=(".'"SELECT id_lien FROM lienparente WHERE sexe_particulier='."'M'".'")');
        } else {
            mysqli_query($co, "DELETE FROM lienpersonne where personne=$p AND lien=(".'"SELECT id_lien FROM lienparente WHERE sexe_particulier='."'F'".'")');
        }
        mysqli_query($co, "INSERT INTO historiquepersonne (texte, date_historique, heure_historique, supprime, personne_concerne, compte_concerne) VALUES ('Vous avez modifié la personne ; ce qui affecte peut-être la suppression de quelques liens de parenté',curdate(),curtime(),false,$p,$c)");
        if ($sexe == 'F'){
            $s = "Femme";
        } else {
            $s = "Homme";
        }
        $headers[] = 'MIME-Version: 1.0';
     $headers[] = 'Content-type: text/html; charset=iso-8859-1';

     // En-têtes additionnels
     $headers[] = 'From: noreply@paris-bethanie.fr <noreply@paris-bethanie.fr>';
     $headers[] = 'Cc: noreply@paris-bethanie.fr <noreply@paris-bethanie.fr>';
	mail($mel,"Inscription","Bonjour, <br /><br />L'église protestante de Béthanie a modifié vos informations suivantes :<br /><br />
    <li><b>Sexe :</b> $s</li><br /><li><b>Nom :</b> ".ucfirst(strtolower($_POST['nom']))."</li><br />
    <li><b>Prénom :</b> ".ucfirst(strtolower($_POST['prenom']))."</li><br /><li><b>Date de naissance :</b> $date </li><br />
    <li><b>Mail :</b> $mel </li><br /><br />Cordialement,<br /><br />Le conseil présbytéral<br /><img src='../images/logo.png' />",implode("\r\n", $headers));
    
        echo "Succès";
    } else {
	echo "Echec";
	}
?>