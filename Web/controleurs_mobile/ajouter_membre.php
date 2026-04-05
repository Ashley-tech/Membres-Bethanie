<?php
    require_once('../modeles/connexion.php');
    require_once('../modeles/compte.php');
    session_start();
    $coBd = new Connexion("paris-bethanie_membre_test");
    $co = $coBd->connexion() or die ("Erreur de connexion");
    
    if (!empty($_GET['compte'])) {
        $c = $_GET['compte'];

        $sexe = $_POST['sexe'];
        $nom = str_replace("'","''",ucfirst(strtolower($_POST['nom'])));
        $prenom = str_replace("'","''",ucfirst(strtolower($_POST['prenom'])));
        $date = $_POST['naissance'];
        $mel = $_POST['mel'];
    
        mysqli_query($co,"INSERT INTO personne (sexe, nom, prenom, date_naissance, mel, statut_personne) VALUES ('$sexe','$nom','$prenom','$date','$mel','Inscrit')") or die ("Création d'un membre impossible !");
        $id = mysqli_insert_id($co);
        mysqli_query($co, "INSERT INTO historiquepersonne (texte, date_historique, heure_historique, supprime, personne_concerne, compte_concerne) VALUES ('Vous avez inséré une personne : $prenom $nom',curdate(),curtime(),false,$id,$c)");

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
	mail($mel,"Inscription","Bonjour, <br /><br />Vous venez de vous inscrire pour devenir membre de l'église protestante de Béthanie.<br /><br />
    Voici vos informations vous concernant :<br />
    <li><b>Sexe :</b> $s</li><br /><li><b>Nom :</b> ".ucfirst(strtolower($_POST['nom']))."</li><br />
    <li><b>Prénom :</b> ".ucfirst(strtolower($_POST['prenom']))."</li><br /><li><b>Date de naissance :</b> $date </li><br />
    <li><b>Mail :</b> $mel </li><br />
    <li><b>Téléphone portable personnel :</b></li><br />
    <li><b>Téléphone fixe personnel :</b></li><br />
    <li><b>Téléphone fax personnel :</b></li><br />
    <li><b>Téléphone portable professionnel :</b></li><br />
    <li><b>Téléphone fixe professionnel :</b></li><br />
    <li><b>Téléphone fax professionnel :</b></li><br />
    <li><b>Adresse :</b></li><br />
    <li><b>Complément d'adresse :</b></li><br />
    <li><b>Code postal :</b></li><br />
    <li><b>Ville :</b></li><br />
    <li><b>Quartier de la ville :</b></li><br />
    <li><b>Numéro de boîte ou numéro d'appartement :</b></li><br /><br />Cordialement,<br /><br />Le conseil présbytéral<br /><img src='../images/logo.png' />",implode("\r\n", $headers));
    
        echo "Succès";
    }
?>