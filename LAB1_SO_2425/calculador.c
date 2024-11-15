/* -----------------------------------------------------------------------
PRA1. Processos, pipes i senyals: Primers
Codi font: calculador.c
Eudald Hernández Anglada
Arnau Farràs Moreno
---------------------------------------------------------------------- */

/*
----------------------------------------------------------------------
--- L L I B R E R I E S ------------------------------
----------------------------------------------------------------------
*/
#include <stdio.h> /* sprintf */

// pid_t
#include <sys/types.h>
#include <unistd.h>

#include <stdlib.h> /* exit, EXIT_SUCCESS, ...*/
#include <string.h> /* strlen */
#include <unistd.h> /* STDOUT_FILENO */
#include <errno.h>  /* errno */
#include <signal.h> 
#include <fcntl.h>  /* O_NONBLOCK */

/*
----------------------------------------------------------------------
--- C O N S T A N T S------ ------------------------------------------
----------------------------------------------------------------------
*/
#define MIDA_MAX_CADENA 1024

#define INVERTIR_COLOR "\e[7m"
#define FI_COLOR "\e[0m"
#define MIDA_MAX_CADENA_COLORS 1024
#define FORMAT_TEXT_ERROR "\e[1;48;5;1;38;5;255m"

/*
----------------------------------------------------------------------
--- C A P Ç A L E R E S   D E   F U N C T I O N S --------------------
----------------------------------------------------------------------
*/
void ComprovarPrimer(int nombre);
void ImprimirInfoCalculador(char *text);
void ImprimirError(char *text);
void sigterm_handler(int signal);

/*
----------------------------------------------------------------------
--- D E F I N I C I Ó   D E   T I P U S ------------------------------
----------------------------------------------------------------------
*/
typedef struct 
{
    pid_t pid_calculador;
    int num;
    int esPrimer;
} t_resultat;

/*
----------------------------------------------------------------------
--- V A R I A B L E S   G L O B A L S --------------------------------
----------------------------------------------------------------------
*/
unsigned char numControlador;
pid_t pidPropi;
char cadena[MIDA_MAX_CADENA];
char dadesCalculador[MIDA_MAX_CADENA];
int quantsPrimers = 0;
int esPrimer;

char taulaColors[8][MIDA_MAX_CADENA_COLORS] = {
    "\e[01;31m", // Vermell
    "\e[01;32m", // Verd
    "\e[01;33m", // Groc
    "\e[01;34m", // Blau
    "\e[01;35m", // Magenta
    "\e[01;36m", // Cian
    "\e[00;33m", // Taronja
    "\e[1;90m"   // Gris fosc
};

/*
----------------------------------------------------------------------
--- P R O G R A M A   P R I N C I P A L ------------------------------
----------------------------------------------------------------------
*/
int main(int argc, char *argv[])
{   
    pidPropi = getpid();
    numControlador = atoi(argv[1]);
    numControlador++;

    /* Mostrar capçalera */
    sprintf(cadena, "[Calculador %u-pid:%u]> Calculador %u activat!\n", numControlador, pidPropi, numControlador);
    ImprimirInfoCalculador(cadena);

    /* Tractar senyal SIGTERM */
    if (signal(SIGTERM, sigterm_handler) == SIG_ERR)
    {
        perror("Error handling SIGTERM signal.");
        exit(EXIT_FAILURE);
    }

    int nombre;

    /* Llegir del pipe fins EOF */
    while (read(11, &nombre, sizeof(int)) > 0) ComprovarPrimer(nombre);

    /* Ja hem llegit i escrit ==> tanquem fds */
    close(11);
    close(20);

    /* Espera el senyal SIGTERM del pare */
    pause();

    perror("Error handling signals.");
    exit(EXIT_FAILURE);
}

void ComprovarPrimer(int nombre)
{
    int i = 2;
    esPrimer = 1;
    t_resultat resultat;

    if (nombre > 2)
    {
        do
        {
            if (nombre % i == 0)
                esPrimer = 0;

            i++;

        } while (i < nombre && esPrimer == 1);
    }

    /* Prepares el resultat per enviar-lo */
    resultat.pid_calculador = pidPropi;
    resultat.num = nombre;
    resultat.esPrimer = esPrimer;

    /* Envia el resultat pel pipe de respostes */
    if (write(20, &resultat, sizeof(t_resultat)) == -1)
    {
        sprintf(cadena, "Error enviant el resultat del nombre %d.", nombre);
        perror(cadena);
        exit(EXIT_FAILURE);
    }

    if (esPrimer == 1) quantsPrimers++;
}

void ImprimirInfoCalculador(char *text)
{
    unsigned char i;
    char info[numControlador * 3 + strlen(dadesCalculador) + strlen(text) + 1];
    char infoColor[numControlador * 3 + strlen(dadesCalculador) + strlen(text) + 1 + MIDA_MAX_CADENA_COLORS * 2];

    for (i = 0; i < numControlador * 3; i++)
        info[i] = ' ';

    for (i = 0; i < strlen(dadesCalculador); i++)
        info[i + numControlador * 3] = dadesCalculador[i];

    for (i = 0; i < strlen(text); i++)
        info[i + numControlador * 3 + strlen(dadesCalculador)] = text[i];

    info[numControlador * 3 + strlen(dadesCalculador) + strlen(text)] = '\0';

    sprintf(infoColor, "%s%s%s", taulaColors[(numControlador - 1) % 8], info, FI_COLOR);

    if (write(STDOUT_FILENO, infoColor, strlen(infoColor)) == -1)
    {
        perror("ERROR write ImprimirInfoControlador");
        exit(EXIT_FAILURE);
    }
}

/* Enviar primers comptats com a codi d'acabament */
void sigterm_handler(int signal) { exit(quantsPrimers); }