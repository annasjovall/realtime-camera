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
  while(1){
    int comm_fd = accept(listen_fd, (struct sockaddr*) NULL, NULL);
    char byte1[1];
    read(listen_fd, byte1, 1);
    printf("%c\n",byte1[0]);
    printf("%d\n", byte1[0] == 'a');
    //printf("annastar - %s", byte1);
  /*  frame* camera_frame = camera_get_frame(cam);
    byte* camera_byte = get_frame_bytes(camera_frame);
    size_t frame_size = get_frame_size(camera_frame);
    unsigned long long time_stamp = get_frame_timestamp(camera_frame);
    unsigned char bytes[8];
    bytes[0] = (time_stamp >> 56) & 0xFF;
    bytes[1] = (time_stamp >> 48) & 0xFF;
    bytes[2] = (time_stamp >> 40) & 0xFF;
    bytes[3] = (time_stamp >> 32) & 0xFF;
    bytes[4] = (time_stamp >> 24) & 0xFF;
    bytes[5] = (time_stamp >> 16) & 0xFF;
    bytes[6] = (time_stamp >> 8) & 0xFF;
    bytes[7] = time_stamp & 0xFF;*/
    //write(comm_fd, time_stamp, 100);
    //write(comm_fd, camera_byte, frame_size);
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
