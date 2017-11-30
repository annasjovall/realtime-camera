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


struct camera_state {
  byte* camera_byte;
  size_t frame_size;
};


void* camera_open_task(void *state)
{
  struct sockaddr_in servaddr;
  int listen_fd = socket(AF_INET, SOCK_STREAM, 0);
  bzero( &servaddr, sizeof(servaddr));
  servaddr.sin_family = AF_INET;
  servaddr.sin_addr.s_addr = htons(INADDR_ANY);
  servaddr.sin_port = htons(22000);
  bind(listen_fd, (struct sockaddr *) &servaddr, sizeof(servaddr));
  listen(listen_fd, 10);
  camera* cam = camera_open();
  struct camera_state* s = state;
  printf("2\n");
  while(1){
    int comm_fd = accept(listen_fd, (struct sockaddr*) NULL, NULL);
    frame* camera_frame = camera_get_frame(cam);
    byte* camera_byte = get_frame_bytes(camera_frame);
    size_t frame_size = get_frame_size(camera_frame);
    write(comm_fd, camera_byte, frame_size);
  }
  return (void*) (intptr_t) 0;
}


int main(int argc, char *argv[])
{

  printf("4\n");
  pthread_t camera_open_thread;
  struct camera_state state;
  pthread_create(&camera_open_thread, NULL, camera_open_task, &state);
  pthread_join(camera_open_thread, NULL);
  printf("4\n");
  return 0;
}
