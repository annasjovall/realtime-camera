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
  int camera_is_open;
};

struct header {
  int size;
};

void init_global_state(struct global_state* state)
{
    struct global_state* s = state;
    s->cam = NULL;
}

void* read_task(void *state)
{
  printf("READ THREAD STARTED\n");
  struct sockaddr_in servaddr;
  int listen_fd = socket(AF_INET, SOCK_STREAM, 0);
  bzero( &servaddr, sizeof(servaddr));
  servaddr.sin_family = AF_INET;
  servaddr.sin_addr.s_addr = INADDR_ANY;
  servaddr.sin_port = htons(22000);
  printf("READ THREAD CONTINUES\n");
  bind(listen_fd, (struct sockaddr *) &servaddr, sizeof(servaddr));
  listen(listen_fd, 10);
  int comm_fd_read = accept(listen_fd, (struct sockaddr*) NULL, NULL);
  printf("ACCEPTED\n");
  while (1) {
    usleep(500000);
    char read_byte[10];
    read(comm_fd_read, read_byte, 10);
  }
  return (void*) (intptr_t) 0;
}

void* write_task(void *state)
{
  printf("WRITE THREAD STARTED\n");
  camera* cam = camera_open();
  if (!cam) {
    printf("Failed to create camera\n");
  }
  struct sockaddr_in servaddr;
  int listen_fd = socket(AF_INET, SOCK_STREAM, 0);
  bzero( &servaddr, sizeof(servaddr));
  servaddr.sin_family = AF_INET;
  servaddr.sin_addr.s_addr = INADDR_ANY;
  servaddr.sin_port = htons(19999);
  bind(listen_fd, (struct sockaddr *) &servaddr, sizeof(servaddr));
  listen(listen_fd, 10);
  int comm_fd_write = accept(listen_fd, (struct sockaddr*) NULL, NULL);
  while(1) {
    usleep(1000);
    frame* camera_frame = camera_get_frame(cam);
    byte* camera_byte = get_frame_bytes(camera_frame);
    size_t frame_size = get_frame_size(camera_frame);
    unsigned long long time_stamp = get_frame_timestamp(camera_frame);
    byte bytes[12];
    bytes[0] = (time_stamp >> 56) & 0xFF;
    bytes[1] = (time_stamp >> 48) & 0xFF;
    bytes[2] = (time_stamp >> 40) & 0xFF;
    bytes[3] = (time_stamp >> 32) & 0xFF;
    bytes[4] = (time_stamp >> 24) & 0xFF;
    bytes[5] = (time_stamp >> 16) & 0xFF;
    bytes[6] = (time_stamp >> 8) & 0xFF;
    bytes[7] = time_stamp & 0xFF;
    bytes[8] = (frame_size >> 24) & 0xFF;
    bytes[9] = (frame_size >> 16) & 0xFF;
    bytes[10] = (frame_size >> 8) & 0xFF;
    bytes[11] = frame_size & 0xFF;
    byte* package[] = &bytes + camera_byte;
    //write(comm_fd, time_stamp, 100);
    char read_byte[1];

    if(camera_frame) {
      //write(comm_fd_write, bytes, 12);
      write(comm_fd_write, package, frame_size + 12);
      frame_free(camera_frame);
    }
  }
  return (void*) (intptr_t) 0;
}

// void* camera_open_task(void *state)
// {
//   struct global_state* s = state;
//   s->cam = camera_open();
//   if (!s->cam) {
//     printf("Failed to create camera");
//   }
//   s->camera_is_open = 1;
//   pthread_cond_broadcast(&global_cond);
//   return (void*) (intptr_t) 0;
// }


int main(int argc, char *argv[])
{
  printf("PROGRAM INIT\n");
  struct global_state state;
  pthread_t thread_ids[2];

  pthread_create(&thread_ids[0], NULL, read_task, &state);
  pthread_create(&thread_ids[1], NULL, write_task, &state);

  pthread_join(thread_ids[0], NULL);
  pthread_join(thread_ids[1], NULL);

  return 0;
}
