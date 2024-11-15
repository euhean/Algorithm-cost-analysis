/* -----------------------------------------------------------------------
PRA1. Processos, pipes i senyals: Primers
Codi font: controlador.c
Eudald Hernández Anglada
Arnau Farràs Moreno
---------------------------------------------------------------------- */

/*
----------------------------------------------------------------------
--- L L I B R E R I E S ------------------------------
----------------------------------------------------------------------
*/
#include <stdio.h> /* sprintf*/

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

#define INVERTIR_COLOR "\e[7m"
#define FI_COLOR "\e[0m"
#define MIDA_MAX_CADENA_COLORS 1024
#define FORMAT_TEXT_ERROR "\e[1;48;5;1;38;5;255m"

/*
----------------------------------------------------------------------
--- C A P Ç A L E R E S   D E   F U N C T I O N S --------------------
----------------------------------------------------------------------
*/
void ImprimirInfoControlador(char *text);
void ImprimirError(char *text);

/*
----------------------------------------------------------------------
--- V A R I A B L E S   G L O B A L S --------------------------------
----------------------------------------------------------------------
*/
char capInfoControlador[MIDA_MAX_CADENA];

/* Variable tipus estructura pels resultats */
typedef struct 
{
    pid_t pid_calculador;
    int num;
    int esPrimer;
} t_resultat;

/*
----------------------------------------------------------------------
--- P R O G R A M A   P R I N C I P A L ------------------------------
----------------------------------------------------------------------
*/
int main(int argc, char *argv[])
{
    unsigned short int numCalculadors;
    unsigned char i;
    pid_t pid;
    int estatWait;
    char cadena[MIDA_MAX_CADENA];
    t_resultat resultat;
    int nombrePrimersPipe = 0, nombrePrimersExit = 0;

    /* Ignorar senyal SIGINT */
    signal(SIGINT, SIG_IGN);
    /* Ignorar senyal SIGQUIT */
    signal(SIGQUIT, SIG_IGN);

    /* Creació dels pipes */
    int pipe_respostes[2], pipe_nombres[2];

    if (pipe(pipe_respostes) == -1 || (pipe(pipe_nombres) == -1)) 
    {
        perror("Error en la creació dels pipes.");
        exit(EXIT_FAILURE);
    }

    /* Gestió de paràmetres incorrectes */
    if (argc != 3) 
    {
        sprintf(cadena, "Us: %s <nombre processos calculadors> <darrer nombre enter per a la seqüència 2 a M>\n\nPer exemple: %s 3 11\n\n", argv[0], argv[0]);

        write(1, cadena, strlen(cadena));
        exit(2);
    }

    sprintf(capInfoControlador, "[%s-pid:%u]> ", argv[0], getpid());

    numCalculadors = atoi(argv[1]);

    /* Array d'identificadors de procés */
    pid_t child_pids[numCalculadors + 1];

    ImprimirInfoControlador("* * * * * * * * * *  I N I C I  * * * * * * * * * *\n");

    sprintf(cadena, "Processos calculadors: %u.\n\n", numCalculadors);
    ImprimirInfoControlador(cadena);

    /* Creació procés generador */
    switch (pid = fork())
    {
        case -1:
            ImprimirError("Error creació generador.");

        case 0:
            /* Tancament fd no utilitzats */
            close(pipe_respostes[0]);
            close(pipe_respostes[1]);
            close(pipe_nombres[0]);
            /* Redireccionament escriptura */
            dup2(pipe_nombres[1], 10);
            close(pipe_nombres[1]);
            /* Recobriment generador */
            execl("./generador", "./generador", argv[2], NULL);

            perror("Error execl fill.");

        default:
            /* Guardar el pid fill */
            child_pids[0] = pid;
    }

    for (i = 0; i < numCalculadors; i++)
    {
        /* Creació calculadors */
        switch (pid = fork())
        {
        case -1:
            sprintf(cadena, "Error creació calculador %u.", i + 1);
            ImprimirError(cadena);

        case 0:
            /* Tancament fd no utilitzats */
            close(pipe_respostes[0]);
            close(pipe_nombres[1]);
            /* Redireccionament escriptura respostes i lectura nombres */
            dup2(pipe_respostes[1], 20);
            close(pipe_respostes[1]);
            dup2(pipe_nombres[0], 11);
            close(pipe_nombres[0]);

            /* Recobriment calculador */
            sprintf(cadena, "%u", i);
            execl("./calculador", "./calculador", &cadena, NULL);

            sprintf(cadena, "Error execl fill %u.", i + 1);
            ImprimirInfoControlador(cadena);
        
        default:
            /* Guardar els pids fills */
            child_pids[i + 1] = pid;
           
            sprintf(cadena, "Activació calculador %u (pid: %u)\n", i + 1, pid);
            ImprimirInfoControlador(cadena);
        }
    }

    /* Tancament fd no utilitzats */
    close(pipe_nombres[0]);
    close(pipe_nombres[1]);
    /* Tancar extrem escriptura */
    close(pipe_respostes[1]);
    /* Redireccionament lectura */
    dup2(pipe_respostes[0], 21);
    close(pipe_respostes[0]);

    while(read(21, &resultat, sizeof(t_resultat)) > 0)
    {
        /* Mostrar respostes calculador pel terminal */
        if(resultat.esPrimer == 1) 
        {
            sprintf(cadena, "Rebut del calculador (pid-%u) nombre %u és primer? Sí\n", resultat.pid_calculador, resultat.num);
            /* Comptar quants primers per pipes */
            nombrePrimersPipe++;      
        }
        else sprintf(cadena, "Rebut del calculador (pid-%u) nombre %u és primer? No\n", resultat.pid_calculador, resultat.num);
        ImprimirInfoControlador(cadena);
    }

    /* Envia la senyal SIGTERM a tots els fills */
    for(i = 0; i <= numCalculadors; i++) kill(child_pids[i], SIGTERM);

    /* Comptar quants primers per exits */
    for(i = 0; i <= numCalculadors; i++) 
    {
        /* Esperar l'acabament dels fills */
        wait(&estatWait);
        nombrePrimersExit += WEXITSTATUS(estatWait);
    }

    /* Mostrar respostes calculador pel terminal */
    sprintf(cadena, "nombrePrimersPipe = %u nombrePrimersExit = %u \n", nombrePrimersPipe, nombrePrimersExit);
    ImprimirInfoControlador(cadena);

    ImprimirInfoControlador("* * * * * * * * * *  F I  * * * * * * * * * *\n");

    exit(EXIT_SUCCESS);
}

void ImprimirInfoControlador(char *text)
{
    char infoColor[strlen(capInfoControlador) + strlen(text) + MIDA_MAX_CADENA_COLORS * 2];

    sprintf(infoColor, "%s%s%s%s", INVERTIR_COLOR, capInfoControlador, text, FI_COLOR);

    if (write(1, infoColor, strlen(infoColor)) == -1)
        ImprimirError("ERROR write ImprimirInfoControlador");
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