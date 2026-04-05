<?php
    session_start();
    require_once("../modeles/connexion.php");
	require_once("../modeles/compte.php");
    $coBd = new Connexion("paris-bethanie_membre_test");
	$co = $coBd->connexion() or die("Erreur de connexion !");
    if (isset($_GET['compte'])){
        $c = $_GET['compte'];
        $p = $_GET['personne'];

        mysqli_query($co, "UPDATE personne SET statut_personne='Inscrit' WHERE id_personne = $p");
        mysqli_query($co, "INSERT INTO historiquepersonne (texte, date_historique, heure_historique, supprime, personne_concerne, compte_concerne) VALUES ('Vous avez réinscrit une personne',curdate(),curtime(),false, $p,$c)");

        $result = mysqli_query($co,"SELECT mel,sexe,nom,prenom FROM personne WHERE id_personne=$p");
        while ($l = mysqli_fetch_assoc($result)){
            $mel = $l['mel'];
            $nom = strtoupper($e['nom']);
            $prenom = $e['prenom'];
            if ($e['sexe'] == 'M'){
                $sexe = 'M.';
            } else {
                $sexe = "Mme";
            }
        }$headers[] = 'MIME-Version: 1.0';
     $headers[] = 'Content-type: text/html; charset=iso-8859-1';
        $headers[] = 'From: noreply@paris-bethanie.fr <noreply@paris-bethanie.fr>';
     $headers[] = 'Cc: noreply@paris-bethanie.fr <noreply@paris-bethanie.fr>';
	mail($mel,"Réinscription","Bonjour $sexe $nom $prenom, <br /><br />Vous venez de vous réinscrire à l'église protestante de Béthanie.<br />Nous sommes ravis que vous redeveniez membre.<br /><br />A bientôt<br /><br />Cordialement,<br /><br />Le conseil présbytéral<br /><img src='../images/logo.png' />",implode("\r\n", $headers));
        echo "Succès";
    } else {
	echo "Echec";
	}
?>