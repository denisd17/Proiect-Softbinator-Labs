# Proiect-Softbinator-Labs
Proiect realizat in cadrul Softbinator Labs 2022.

## Integrari
- Baza de date utilizata este **MariaDB**
- Pozele de profil si facturile sunt stocate intr-un **AWS S3 Bucket**
- Pentru autentificare am utilizat **Keycloak**

## Descriere functionalitati
Pe platforma exista 4 tipuri de utilizatori:
- User normal
- Admin
- Admin de organizatie
- Moderator de organizatie

La crearea unui cont, utilizatorul primeste pe adresa de mail specificata, un mail de bun venit.
Poza de profil a acestuia este stocata pe cloud, fiind stearsa in momentul stergerii contului.
Utilizatorul poate crea organizatii caritabile, devenind automat administrator al acestora si primind rolul aferent.

In cadrul organizatiei, administratorul poate adauga / sterge moderatori. 
Fiecare utilizator adaugat in echipa primeste rolul de moderator, iar la stergere din echipa / stergerea organizatiei acesta ii va fi revocat (in cazul in care nu mai este moderator in nici o alta organizatie).

Administratorul organizatiei poate crea proiecte, ce pot fi de doua tipuri:
- eveniment caritabil (event)
- strangere de fonduri (fundraiser)

#### Eveniment caritabil
Are un nume, o descriere, numar de bilete disponibile si pretul per bilet.
Utilizatorii pot cumpara bilete.
La achizitia de bilete, se genereaza automat o factura ce va fi stocata pe cloud, iar un link catre aceasta va fi trimisa userului pe mail.

#### Strangere de fonduri
Are un nume, o descriere si o suma tinta.
Utilizatorii pot dona, primind la fel linkul catre factura pe mail.

Pentru ambele tipuri de proiecte pot fi vizualizate sumele stranse, iar pentru eveniment si nr. de bilete ramase.


Atat administratorul organizatiei cat si moderatorii acesteia pot crea postari asociate proiectelor.
Utilizatorii pot lasa comentarii la aceste postari.
