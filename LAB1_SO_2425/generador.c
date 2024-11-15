/* -----------------------------------------------------------------------
PRA1. Processos, pipes i senyals: Primers
Codi font: generador.c
Eudald Hernández Anglada
Arnau Farràs Moreno
---------------------------------------------------------------------- */

/*
----------------------------------------------------------------------
--- L L I B R E R I E S ------------------------------
----------------------------------------------------------------------
*/
#include <stdio.h> /* sprintf */

// fork, pid_t, wait, ..
#include <sys/types.h>
#include <unistd.h>
// signals
#include <signal.h>

#include <stdlib.h> /* exit, EXIT_SUCCESS, ...*/
#include <string.h> /* strlen */

#include <sys/wait.h> /* wait */
#include <errno.h>    /* errno */

/*
----------------------------------------------------------------------
--- C O N S T A N T S------ ------------------------------------------
----------------------------------------------------------------------
*/
#define MIDA_MAX_CADENA 1024
#define FI_COLOR "\e[0m"
#define MIDA_MAX_CADENA_COLORS 1024
#define FORMAT_TEXT_ERROR "\e[1;48;5;1;38;5;255m"

/*
----------------------------------------------------------------------
--- C A P Ç A L E R E S   D E   F U N C T I O N S --------------------
----------------------------------------------------------------------
*/
void ImprimirInfoGenerador(char *text);
void ImprimirError(char *text);
void sigquit_handler(int signal);
void sigterm_handler(int signal);

/*
----------------------------------------------------------------------
--- V A R I A B L E S   G L O B A L S --------------------------------
----------------------------------------------------------------------
*/
char capInfoControlador[MIDA_MAX_CADENA];
char cadena[MIDA_MAX_CADENA];
int M;

/*
----------------------------------------------------------------------
--- P R O G R A M A   P R I N C I P A L ------------------------------
----------------------------------------------------------------------
*/
int main(int argc, char *argv[]) 
{
    M = atoi(argv[1]);

    sprintf(capInfoControlador, "[%s-pid:%u]> ", argv[0], getpid());

    /* Mostrar missatge per pantalla */
    sprintf(cadena, "Activat i esperant la senyal SIGQUIT (Ctrl+4 al teclat) per a generar la seqüència 2 a %u.\n", M);
    ImprimirInfoGenerador(cadena);

    if (signal(SIGQUIT, sigquit_handler) == SIG_ERR) 
        ImprimirError("Failed to set handler for SIGQUIT signal.");

    /* Espera indefinidament rebuda SIGQUIT */
    pause();

    if (signal(SIGTERM, sigterm_handler) == SIG_ERR)
        perror("Failed to set handler for SIGTERM signal.");

    /* Espera indefinidament rebuda SIGTERM */
    pause();

    ImprimirError("Error tractament o rebuda de senyals.");
}

void ImprimirInfoGenerador(char *text)
{
    char infoColor[strlen(capInfoControlador) + strlen(text) + MIDA_MAX_CADENA_COLORS * 2];

    sprintf(infoColor, "\033[37m%s%s\033[0m", capInfoControlador, text);

    if (write(1, infoColor, strlen(infoColor)) == -1)
        ImprimirError("ERROR write ImprimirInfoGenerador.");
}

void ImprimirError(char *text)
{
    char infoColorError[strlen(capInfoControlador) + strlen(text) + MIDA_MAX_CADENA_COLORS * 2];

    sprintf(infoColorError, "%s%s%s: %s%s\n", FORMAT_TEXT_ERROR, capInfoControlador, text, strerror(errno), FI_COLOR);
    write(2, "\n", 1);
    write(2, infoColorError, strlen(infoColorError));
    write(2, "\n", 1);

    exit(EXIT_FAILURE);
}

void sigquit_handler(int signal)
{
    /* Escriu la seqüència d'enters 2 a M en el descriptor de fitxer 10 */
    for (int i = 2; i <= M; i++) 
        if (write(10, &i, sizeof(int)) == -1) 
            ImprimirError("Error en escriure al pipe de nombres.");
    close(10);
}

void sigterm_handler(int signal) { exit(EXIT_SUCCESS); }