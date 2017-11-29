/* A simple server in the internet domain using TCP
   The port number is passed as an argument */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include "camera.h"

struct global_state {
    camera* cam;
};


// int try_open_camera(struct global_state* state)
// {
//     state->cam = camera_open();
//     if (!state->cam){ // Check if null
//         printf("axism3006v: Stream is null, can't connect to camera");
//         return ERR_OPEN_STREAM;
//     }
//     return 0;
// }

int main(int argc, char *argv[])
{
  char str[] = "Hello World";
  struct global_state state;
  //Struct to hold IP Address and Port Numbers
  struct sockaddr_in servaddr;
  //Vill skicka över AF_INET, typen SOCK_STREAM sen 0,
  //Each server needs to “listen” for connections. The above
  //function creates a socket with AF_INET ( IP Addressing )
  //and of type SOCK_STREAM. Data from all devices wishing to
  //connect on this socket will be redirected to listen_fd.
  int listen_fd = socket(AF_INET, SOCK_STREAM, 0);
  //Clear servaddr ( Mandatory ).
  bzero( &servaddr, sizeof(servaddr));
  // Set Addressing scheme to – AF_INET ( IP )
  // Allow any IP to connect – htons(INADDR_ANY)
  // Listen on port 22000 – htons(22000)
  servaddr.sin_family = AF_INET;
  servaddr.sin_addr.s_addr = htons(INADDR_ANY);
  servaddr.sin_port = htons(22000);
  //Prepare to listen for connections from address/port
  //specified in sockaddr ( Any IP on port 22000 ).
  bind(listen_fd, (struct sockaddr *) &servaddr, sizeof(servaddr));
  // Start Listening for connections , keep at the most 10 connection
  // requests waiting.If there are more than 10 computers wanting to connect
  // at a time, the 11th one fails to.
  listen(listen_fd, 10);
  //Accept a connection from any device who is willing to connect, If there
  //is no one who wants to connect , wait. A file descriptor is returned.
  //This can finally be used to communicate , whatever is sent by the device
  // accepted can be read from comm_fd, whatever is written to comm_fd is
  //sent to the other device.
  int comm_fd = accept(listen_fd, (struct sockaddr*) NULL, NULL);
  state.cam = camera_open();

  if(!state.cam){
    printf("stream is null ,cannot connect to camera");
  }
  //
  // frame* fram = camera_get_frame(cam);
  //
  // if(fram){
  //   printf("we got a frame!");
  // }
  //
  // char* byt = get_frame_bytes(fram);
  // size_t frameSize = get_frame_size(fram);

  while (1) {
    write(comm_fd, str, 11);
  }

}
