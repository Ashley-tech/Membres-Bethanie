<?php
    require_once('../modeles/connexion.php');
    require_once('../modeles/compte.php');
    session_start();
    $coBd = new Connexion("paris-bethanie_membre_test");
    $co = $coBd->connexion() or die ("Erreur de connexion");
    
    if (!empty($_GET['personne']) && !empty($_GET['compte'])){
        $c = $_GET['compte'];
        $p = $_GET['personne'];

        $result = mysqli_query($co, "SELECT sexe, nom,prenom,mel FROM personne WHERE id_personne=$p");
        while ($l = mysqli_fetch_assoc($result)){
            $sexe = $l['sexe'];
            $nom = $l['nom'];
            $prenom = $l['prenom'];
            $mel = $l['mel'];
        }

        if (!empty($_POST['adresse'])){
            $adresse = "'".str_replace("'","''",$_POST['adresse'])."'";
        } else {
            $adresse = "NULL";
        }

        if (!empty($_POST['complement'])){
            $cadresse = "'".str_replace("'","''",$_POST['complement'])."'";
        } else {
            $cadresse = "NULL";
        }

        if (!empty($_POST['cp'])){
            $cp = "'".str_replace("'","''",$_POST['cp'])."'";
        } else {
            $cp = 'NULL';
        }

        if (!empty($_POST['ville'])){
            $ville = "'".str_replace("'","''",$_POST['ville'])."'";
        } else {
            $ville = 'NULL';
        }

        if (!empty($_POST['quartier'])){
            $qv = "'".str_replace("'","''",$_POST['quartier'])."'";
        } else {
            $qv = 'NULL';
        }

        if (!empty($_POST['boite'])) {
            $ba = "'".str_replace("'","''",$_POST['boite'])."'";
        } else {
            $ba = 'NULL';
        }

        mysqli_query($co,"UPDATE personne SET adresse=$adresse,adresse_comp=$cadresse,cp=$cp,ville=$ville,quartierville=$qv,numboite_appt=$ba WHERE id_personne=$p") or die("Modification d'un membre impossible");
                                            
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
    <li><b>Adresse :</b> $adresse </li><br />
    <li><b>Complément d'adresse :</b> $cadresse </li><br />
    <li><b>Code postal :</b> $cp </li><br />
    <li><b>Ville :</b> $ville </li><br />
    <li><b>Quartier de la ville :</b> $qv </li><br />
    <li><b>Numéro de boîte ou numéro d'appartement :</b> $ba </li><br /><br />Cordialement,<br /><br />Le conseil présbytéral<br /><img src='../images/logo.png' />",implode("\r\n", $headers));

        echo "Succès";
    } else {
        echo "Echec";
    }
?>