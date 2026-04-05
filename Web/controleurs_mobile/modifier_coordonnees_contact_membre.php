<?php
    require_once('../modeles/connexion.php');
    require_once('../modeles/compte.php');
    session_start();
    $coBd = new Connexion("paris-bethanie_membre_test");
    $co = $coBd->connexion() or die ("Erreur de connexion");
    
    if (!empty($_GET['compte']) && !empty($_GET['personne'])){
	    $c = $_GET['compte'];
        $p = $_GET['personne'];
        $portperso = $_POST['telperso'];
        $fixeperso = $_POST['telfixeperso'];
        $faxperso = $_POST['telfaxperso'];
        $portpro = $_POST['telpro'];
        $faxpro = $_POST['telfaxpro'];
        $fixepro = $_POST['telfixepro'];

        if (!empty($portperso)) {
            $portperso = "'$portperso'";
        } else {
            $portperso = "NULL";
        }

        if (!empty($fixeperso)) {
            $fixeperso = "'$fixeperso'";
        } else {
            $fixeperso = "NULL";
        }

        if (!empty($faxperso)) {
            $faxperso = "'$faxperso'";
        } else {
            $faxperso = "NULL";
        }

        if (!empty($portpro)) {
            $portpro = "'$portpro'";
        } else {
            $portpro = "NULL";
        }

        if (!empty($fixepro)) {
            $fixepro = "'$fixepro'";
        } else {
            $fixepro = "NULL";
        }

        if (!empty($fixepro)) {
            $faxpro = "'$faxpro'";
        } else {
            $faxpro = "NULL";
        }

        mysqli_query($co,"UPDATE personne SET telfixe=$fixeperso,telportable=$portperso,telfax=$faxperso,telfixepro=$fixepro,telfaxpro=$faxpro,telportablepro=$portpro WHERE id_personne=$p") or die("Modification d'un membre impossible");
        mysqli_query($co, "INSERT INTO historiquepersonne (texte, date_historique, heure_historique, supprime, personne_concerne, compte_concerne) VALUES ('Vous avez modifié la personne ; ce qui affecte peut-être la suppression de quelques liens de parenté',curdate(),curtime(),false,$p,$c)");

                    $result1 = mysqli_query($co,"SELECT sexe,mel,prenom,nom FROM personne WHERE id_personne=$p");
                    while ($e = mysqli_fetch_assoc($result1)){
                        $mel = $e['mel'];
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
	mail($mel,"Modifications","Bonjour $sexe $nom $prenom, <br /><br />L'église protestante a modifié vos informations suivantes :<br /><br />
    <li><b>Téléphone portable personnel :</b> ".$_POST['telperso']."</li><br />
    <li><b>Téléphone fixe personnel :</b> ".$_POST['telfixeperso']."</li><br />
    <li><b>Téléphone fax personnel :</b> ".$_POST['telfaxperso']."</li><br />
    <li><b>Téléphone portable professionnel :</b> ".$_POST['telpro']."</li><br />
    <li><b>Téléphone fixe professionnel :</b> ".$_POST['telfixepro']."</li><br />
    <li><b>Téléphone fax personnel :</b> ".$_POST['telfaxpro']."</li><br /><br />Cordialement,<br /><br />Le conseil présbytéral<br /><img src='../images/logo.png' />",implode("\r\n", $headers));

        echo "Succès";
    } else {
        echo "Echec";
    }
?>