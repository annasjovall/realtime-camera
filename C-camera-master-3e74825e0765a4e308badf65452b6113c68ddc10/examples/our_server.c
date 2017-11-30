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
#include <pthread.h>



// int try_open_camera(struct global_state* state)
// {
//     state->cam = camera_open();
//     if (!state->cam){ // Check if null
//         printf("axism3006v: Stream is null, can't connect to camera");
//         return ERR_OPEN_STREAM;
//     }
//     return 0;
// }

struct global_state {
  camera* cam;
};

void init_global_state(struct global_state* state)
{
    state->cam=NULL;
}


void* camera_open_task(void *cam)
{
  cam = camera_open();
  return (void*) (intptr_t) 0;
}


int main(int argc, char *argv[])
{
  struct sockaddr_in servaddr;
  struct sockaddr_in servaddr2;
  socklen_t socklen;
  //Vill skicka över AF_INET, typen SOCK_STREAM sen 0,
  //Each server needs to “listen” for connections. The above
  //function creates a socket with AF_INET ( IP Addressing )
  //and of type SOCK_STREAM. Data from all devices wishing to
  //connect on this socket will be redirected to listen_fd.
  printf("1\n");
  int listen_fd = socket(AF_INET, SOCK_STREAM, 0);
  bzero( &servaddr, sizeof(servaddr));
  servaddr.sin_family = AF_INET;
  servaddr.sin_addr.s_addr = htons(INADDR_ANY);
  servaddr.sin_port = htons(22000);
  //Prepare to listen for connections from address/port
  //specified in sockaddr ( Any IP on port 22000 ).
  printf("2\n");
  bind(listen_fd, (struct sockaddr *) &servaddr, sizeof(servaddr));
  // Start Listening for connections , keep at the most 10 connection
  // requests waiting.If there are more than 10 computers wanting to connect
  printf("3\n");
  // at a time, the 11th one fails to.
  listen(listen_fd, 10);
  printf("4\n");
  //Accept a connection from any device who is willing to connect, If there
  //is no one who wants to connect , wait. A file descriptor is returned.
  //This can finally be used to communicate , whatever is sent by the device
  // accepted can be read from comm_fd, whatever is written to comm_fd is
  //sent to the other device.
  camera* cam = NULL;
  pthread_t camera_open_thread;
  while (1) {
  if(!pthread_create(&camera_open_thread, NULL, camera_open_task, cam)){
    while (1) {
      printf("4\n");
      int comm_fd = accept(listen_fd, (struct sockaddr*) &servaddr2, &socklen);
      printf("4\n");
      frame* camera_frame = camera_get_frame(cam);
      printf("4\n");
      byte* camera_byte = get_frame_bytes(camera_frame);
      size_t frame_size = get_frame_size(camera_frame);
      size_t frame_height = get_frame_height(camera_frame);
      size_t frame_width = get_frame_width(camera_frame);
      int* frame_size_int = (int*) frame_size;
      printf("Size: %zu\n", frame_size);
      printf("Width: %zu\n", frame_width);
      printf("Heihgt: %zu\n", frame_height);

      printf("Heihgt: %p\n", frame_size_int);
      //write(comm_fd, &frame_size_int, 10);
      write(comm_fd, camera_byte, frame_size);
      frame_free(camera_frame);
    }
  }
  }

}
